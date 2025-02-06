import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import './Styles.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [showError, setShowError] = useState(false);
  const navigate = useNavigate();
  const { setUserEmail } = useContext(UserContext);

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const handleLogin = async () => {
    setShowError(true);
    if (!email) {
      setError('Email is required');
      return;
    }
    if (!validateEmail(email)) {
      setError('Invalid email format');
      return;
    }

    try {
      const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/users`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': email
        }
      });

      console.log('Response:', response);

      if (!response.ok) {
        throw new Error('Email is not correct');
      }

      setUserEmail(email);
      setError('');
      navigate('/home'); // navigate home
    } catch (error) {
      setError(error.message);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      handleLogin();
    }
  };

  const handleRegister = () => {
    navigate('/registration');
  };

  return (
      <div className="container">
        <h1 className="title">Welcome to Officely's Admin Panel!</h1>
        <p className="subtitle">Insert e-mail to log in</p>
        <div className="inputContainer">
          <input
              type="email"
              placeholder="example@gmail.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onKeyPress={handleKeyPress}
              className="input"
          />
        </div>
        <button onClick={handleLogin} className="loginButton">
          Log in
        </button>
        {showError && error && <p className="error">{error}</p>}
        <button onClick={handleRegister} className="registerButton">
          Register
        </button>
      </div>
  );
};

export default LoginPage;