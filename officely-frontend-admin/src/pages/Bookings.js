import React, { useState } from "react";
import Header from "../components/Header";
import SearchBar from "../components/SearchBar";
import BookingsList from "../components/Bookings/BookingsList";

const Bookings = () => {
    const [inputText, setInputText] = useState('');
    const [searchTerm, setSearchTerm] = useState('');

    const onSearch = () => {
        setSearchTerm(inputText);
    };

    return (
        <div className="admin-panel">
            <Header />
            <h1>Bookings Page</h1>
            <p>Filter bookings by ID or office ID</p>
            <div>
                <SearchBar onSearch={onSearch} searchTerm={inputText} onChange={(e) => setInputText(e.target.value)} />
                <BookingsList searchTerm={searchTerm} />
            </div>
        </div>
    );
}

export default Bookings;
