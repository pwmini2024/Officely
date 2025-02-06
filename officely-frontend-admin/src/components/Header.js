import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import homeIcon from '../assets/icons8-home-96.png';
import { UserContext } from '../context/UserContext';

// possibly a header with offics/bookings/users

const Header = () => {
    const navigate = useNavigate();
    const { setUserEmail } = useContext(UserContext);

    const handleLogout = () => {
        navigate('/');
        setUserEmail('');
    }

    return (
        <header className="header">
            <div className="home-icon" onClick={() => navigate('/home')}>
                <img src={homeIcon} alt="Home" style={{ width: '24px', height: '24px' }} />
            </div>
            <button className="button" onClick={handleLogout}>Log out</button>
        </header>
    );
}

export default Header;