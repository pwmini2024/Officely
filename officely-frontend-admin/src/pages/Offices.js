import React, {useContext} from "react";
import Header from "../components/Header";
import './Styles.css';
import SearchBar from "../components/SearchBar";
import OfficesList from "../components/Offices/OfficesList";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {UserContext} from "../context/UserContext";

const Offices = () => {
    const [inputText, setInputText] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();


    const onSearch = () => {
        setSearchTerm(inputText);
    };

    const handleAddOffice = () => {
        navigate('/create-office');
    }


    return (
        <div className="admin-panel">
            <Header />
            <h1>Offices Page</h1>
            <p>Browse and edit offers</p>
            <SearchBar onSearch={onSearch} searchTerm={inputText} onChange={(e) => setInputText(e.target.value)} />
            <OfficesList searchTerm={searchTerm} onAddOffice={handleAddOffice}/>
        </div>
    );
}

export default Offices;
