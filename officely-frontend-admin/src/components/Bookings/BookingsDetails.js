import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../Header';
import { UserContext } from '../../context/UserContext';
import './Bookings.css';

const BookingsDetails = () => {
    const { userEmail } = useContext(UserContext); // Corrected property name
    const navigate = useNavigate();
    const { id } = useParams();
    const [isEditing, setIsEditing] = useState(false); // default is view mode
    const [booking, setBooking] = useState(null); //form data
    const [formData, setFormData] = useState({
        email:userEmail,
        reservation:{
            duration: '',
            startTime: '',
            endTime: '',
            paymentType: '',
            comments: '',
            bookedAt: '',
            officeDto: {
                id: '',
                name: ''
            },
            paid: '',
            paidAt: '',
            priceMultiplier: '',
            pricePerDay: '',
            status: '',
            totalPrice: ''
        }

    });

    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchBooking = async () => {
            try {
                const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/reservations/${id}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': userEmail
                    }
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                setBooking(data);
                setFormData(data);
            } catch (error) {
                setError(error.message);
                console.error('Error fetching booking:', error);
            }
        };

        fetchBooking();
    }, [id]);

    const toggleEditing = () => {
        setIsEditing(!isEditing);
        if (isEditing) {
            setFormData(booking);
        }
    };

    const handleDelete = async () => {
        try {
            const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/reservations/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': userEmail
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            alert('Booking deleted successfully');
            navigate('/bookings');
        } catch (error) {
            console.error('There was an error deleting the booking!', error);
        }
    };

    const handleEdit = () => {
        toggleEditing();
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleCancel = () => {
        setFormData(booking);
        toggleEditing();
    };

    const handleSave = async () => {
        console.log('Saving booking with data:', formData);
        if (formData.paymentType !== 'CASH' && formData.paymentType !== 'CARD') {
            alert('Payment type must be either CASH or CARD');
            return;
        }
        try {
            const requestBody = {
                email: userEmail,
                reservation: {
                    startTime: formData.startTime,
                    endTime: formData.endTime,
                    paymentType: formData.paymentType,
                    comments: formData.comments
                }
            };

            const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/reservations/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': userEmail
                },
                body: JSON.stringify(requestBody)
            });

            console.log(JSON.stringify(requestBody));
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            alert('Booking updated successfully');
            setBooking(formData);
            setIsEditing(false);
        } catch (error) {
            console.error('There was an error updating the booking!', error);
        }
    };

    useEffect(() => {
        const calculateDuration = () => {
            const start = new Date(formData.startTime);
            const end = new Date(formData.endTime);
            const duration = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
            setFormData((prevFormData) => ({
                ...prevFormData,
                duration: duration,
                totalPrice: duration * prevFormData.pricePerDay * prevFormData.priceMultiplier
            }));
        };

        if (formData.startTime && formData.endTime) {
            calculateDuration();
        }
    }, [formData.startTime, formData.endTime, formData.pricePerDay, formData.priceMultiplier]);

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!booking) {
        return <div>Loading...</div>;
    }

    const calculateDuration = (start, end) => {
        const startDate = new Date(start);
        const endDate = new Date(end);
        const timeDifference = Math.abs(endDate - startDate);
        const daysDifference = Math.ceil(timeDifference / (1000 * 3600 * 24));
        return daysDifference;
    }

    const calculatePrice = (pricePerDay, priceMultiplier, duration) => {
        return pricePerDay * priceMultiplier * duration;
    }

    return (
        <div className="booking-details">
            <Header />
            {isEditing ? (
                <div className="booking-edit">
                    <h1>Edit booking</h1>
                    <p>Booking ID: {id}</p>
                    <p>Date from: </p>
                    <input className='date-input' type="date" name="startTime" value={formData.startTime} onChange={handleChange} required />
                    <p>Date to: </p>
                    <input className='date-input' type="date" name="endTime" value={formData.endTime} onChange={handleChange} required />
                    <p>Number of days: {formData.duration} Days</p>
                    <p>Total Price: {formData.totalPrice}</p>
                    <p>Payment type: </p>
                    <select className='payment-input' name="paymentType" value={formData.paymentType} onChange={handleChange} required>
                        <option value="CASH">CASH</option>
                        <option value="CARD">CARD</option>
                    </select>
                    <p>Comments: </p>
                    <input className='input' type="text" name="comments" value={formData.comments} onChange={handleChange} />
                    <p>Booked At: {formData.bookedAt}</p>
                    <p>Office Id: {formData.officeDto.id}</p>
                    <p>Office Name: {formData.officeDto.name}</p>
                    <p>Paid: {formData.paid ? 'Yes' : 'No'}</p>
                    <p>Paid At: {formData.paidAt ? formData.paidAt : 'N/A'}</p>
                    <p>Price Multiplier: {formData.priceMultiplier}</p>
                    <p>Price Per Day: {formData.pricePerDay}</p>
                    <p>Status: {formData.status}</p>
                    <button className="button" onClick={handleCancel}>Cancel</button>
                    <button className="button" onClick={handleSave}>Save</button>
                </div>
            ) : (
                <div>
                    <div className="booking-edit">
                        <button className="back-button" onClick={() => navigate('/bookings')}>
                            ←
                        </button>
                        <h1>Booking Details</h1>
                        <p>Booking ID: {id}</p>
                        <p>Number of days: {formData.duration} Days</p>
                        <p>Date from: {formData.startTime}</p>
                        <p>Date to: {formData.endTime}</p>
                        <p>Payment type: {formData.paymentType}</p>
                        <p>Comments: {formData.comments}</p>
                        <p>Booked At: {formData.bookedAt}</p>
                        <p>Office Id: {formData.officeDto.id}</p>
                        <p>Office Name: {formData.officeDto.name}</p>
                        <p>Paid: {formData.paid ? 'Yes' : 'No'}</p>
                        <p>Paid At: {formData.paidAt ? formData.paidAt : 'N/A'}</p>
                        <p>Price Multiplier: {formData.priceMultiplier}</p>
                        <p>Price Per Day: {formData.pricePerDay}</p>
                        <p>Status: {formData.status}</p>
                        <p>Total Price: {formData.totalPrice}</p>
                        <button className="edit-booking-button" onClick={handleDelete}>Delete</button>
                        <button className="edit-booking-button" onClick={handleEdit}>Edit</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default BookingsDetails;