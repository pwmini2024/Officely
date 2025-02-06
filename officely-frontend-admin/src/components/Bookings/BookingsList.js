import React, { useState, useEffect, useContext } from 'react';
import BookingsListItem from './BookingsListItem';
import SearchCalendar from '../SearchCalendar';
import { UserContext } from '../../context/UserContext';
import './Bookings.css';

const BookingsList = ({ searchTerm }) => {
    const [bookings, setBookings] = useState([]);
    const [filteredBookings, setFilteredBookings] = useState([]);
    const {userEmail} = useContext(UserContext);

    const fetchBookings = async () => {
        try {
            const response = await fetch("https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/reservations", {
                method: "GET",
                headers: {
                    "Authorization": userEmail
                }
            });
            if(!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            setBookings(data);
            setFilteredBookings(data);
        } catch (error) {
            console.error("Error fetching bookings:", error);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const handleFilter = (dateFrom, dateTo) => {
        let filtered = bookings;

        if (dateFrom) {
            filtered = filtered.filter(booking => new Date(booking.startTime) >= new Date(dateFrom));
        }

        if (dateTo) {
            filtered = filtered.filter(booking => new Date(booking.endTime) <= new Date(dateTo));
        }

        setFilteredBookings(filtered);
    };

    const filteredData = filteredBookings.filter((el) => {
        if (searchTerm === '') {
          return el;
        } else {
          return (el.id && el.id.includes(searchTerm)) || 
                 (el.officeDto && el.officeDto.id && el.officeDto.id.includes(searchTerm));
        }
      });
    
    return (
        <div className='booking-list-container'>
            <SearchCalendar onFilter={handleFilter} />
            <ul>
                {filteredData.map(booking => (
                    <BookingsListItem booking={booking} key={booking.id} />
                ))}
            </ul>
        </div>
    );

}
export default BookingsList;