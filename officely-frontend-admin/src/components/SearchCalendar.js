import React, { useState } from 'react';
import '../pages/Styles.css';

const SearchCalendar = ({ onFilter }) => {
    const [dateFrom, setDateFrom] = useState('');
    const [dateTo, setDateTo] = useState('');

    const handleFilter = () => {
        onFilter(dateFrom, dateTo);
    };

    return (
        <div className="search-calendar">
            <input className="calendar-input"
                type="date" 
                value={dateFrom} 
                onChange={(e) => setDateFrom(e.target.value)} 
                placeholder="Date From" 
            />
            <input className="calendar-input"
                type="date" 
                value={dateTo} 
                onChange={(e) => setDateTo(e.target.value)} 
                placeholder="Date To" 
            />
            <button className="calendar-apply-button" onClick={handleFilter}>Apply</button>
        </div>
    );
};

export default SearchCalendar;
