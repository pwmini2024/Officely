import React from 'react';
import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserContext } from '../../context/UserContext';
import './Bookings.css';

const BookingsListItem = ({ booking }) => {
  const navigate = useNavigate();
  const userEmail = useContext(UserContext);

  const handleDelete = async () => {
    try {
      const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/reservations/${booking.id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': userEmail
        },
        body: JSON.stringify({ email: 'marta@mail.com' })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      alert('Booking deleted successfully');

    } catch (error) {
      console.error('There was an error deleting the booking!', error);
    }
  };

  const handleEdit = () => {
    console.log('Edit booking', booking.id);
    navigate(`/booking-details/${booking.id}`);
  };

  const calculateEndDate = (startDate, duration) => {
    const date = new Date(startDate);
    date.setDate(date.getDate() + duration);
    return date.toISOString().split('T')[0];
  };

  return (
    <div className="booking-info">
      <div className='info-piece'>
        <p>Booking ID</p>
        <p className="bookingID">{booking.id}</p>
      </div>
      <div className='info-piece'>
        <p>Number of days</p>
        <p className="duration">{booking.duration} Days</p>
      </div>
      <div className='info-piece'>
        <p>Date from:</p>
        <p className="date">{booking.startTime}</p>
      </div>
      <div className='info-piece'>
        <p>Date to:</p>
        <p className="date">{calculateEndDate(booking.startTime, booking.duration)}</p>
      </div>

      <div className='info-piece'>
        <p>Office Id</p>
        <p className="officeId">{booking.officeDto.id}</p>
      </div>
      <div className='info-piece'>
        <p>Office Name</p>
        <p className="officeName">{booking.officeDto.name}</p>
      </div>
      <div className='info-piece'>
        <p>Status</p>
        <p className="status">{booking.status}</p>
      </div>
      <div className='button-container'>
        <button className="edit-booking-button" onClick={handleEdit}>View details</button>
        {!(booking.status === 'CANCELLED') &&
          <button className="delete-booking-button" onClick={handleDelete}>Cancel booking</button>}
      </div>
    </div>
  );
};

export default BookingsListItem;