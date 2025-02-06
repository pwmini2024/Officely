import React from "react";
import Header from "../components/Header";
import RegistrationForm from "../components/RegistrationForm";
import { useNavigate } from "react-router-dom";

const Registration = () => {
    const navigate = useNavigate();
    return (
        <div className="admin-panel">
            <h1>Registration</h1>
            <p>We are thrilled you want to join Officely!</p>
            <p>Fill in the form below to register.</p>
            <RegistrationForm />
            <button className="button-back" onClick={() => navigate('/')}>Go back to sign in page</button>
        </div>
    );
}

export default Registration;