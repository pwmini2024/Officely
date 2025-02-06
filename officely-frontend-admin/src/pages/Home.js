import React from 'react';
import './Styles.css';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import { UserContext } from '../context/UserContext';

const Home = () => {
  const navigate = useNavigate();
  const {userEmail} = React.useContext(UserContext);

  return (
      <div className="admin-panel">
        <Header />
        <main>
          <h1>This is the Admin Panel</h1>
          <div className="card-container">
            <div className="card">
              <h2>Offices</h2>
              <button className="button" onClick={() => navigate('/offices')}>Select</button>
            </div>
            <div className="card">
              <h2>Bookings</h2>
              <button className="button" onClick={() => navigate('/bookings')}>Select</button>
            </div>
          </div>
        </main>
      </div>
  );
};

export default Home;
