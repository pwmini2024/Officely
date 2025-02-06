import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import './RegistrationForm.css';

const RegistrationForm = () => {
    const [formData, setFormData] = useState({
        email: '',
        name: '',
        surname: '',
        phoneNumber: '',
        birthDate: ''
    });

    const { setUserEmail } = useContext(UserContext);
    const navigate = useNavigate();

    const onCancel = () => {
        setFormData({
            email: '',
            name: '',
            surname: '',
            phoneNumber: '',
            birthDate: ''
        })
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/users', {
                method: 'POST',
                headers: {
                    'Authorization': formData.email,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ user: formData }),
            });

            console.log("creating user:", JSON.stringify({ user: formData }));

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('User registered:', data);

             // Set user email in context and navigate to home
             setUserEmail(formData.email);
             navigate('/home');
        } catch (error) {
            console.error('Error registering user:', error);
        }

    };

    return (
        <form className="registration-form" onSubmit={handleSubmit}>
            <div className="form-group">
                <label>Email:</label>
                <input type="email" name="email" value={formData.email} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label>Name:</label>
                <input type="text" name="name" value={formData.name} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label>Surname:</label>
                <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label>Phone Number:</label>
                <input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} required />
            </div>
            <div className="form-group">
                <label>Birth Date:</label>
                <input type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} required />
            </div>
            <div className="form-actions">
                <button type="button" className="btn btn-cancel" onClick={onCancel}>Cancel</button>
                <button type="submit" className="btn btn-save">Save</button>
            </div>
        </form>
    );
};

export default RegistrationForm;

