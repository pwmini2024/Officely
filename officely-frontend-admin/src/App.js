import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './App.css';
import LoginPage from './pages/LogIn';
import Home from './pages/Home';
import Offices from './pages/Offices';
import Bookings from './pages/Bookings';
import EditOffice from './components/Offices/EditOffice';
import OfficeForm from './components/Offices/OfficeForm';
import BookingsDetails from './components/Bookings/BookingsDetails';
import Registration from './pages/Registration';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<LoginPage />} index />
          <Route path="/home" element={<Home />} />
          <Route path="/offices" element={<Offices />} />
          <Route path="/bookings" element={<Bookings />} />
          <Route path="/edit-office/:id" element={<EditOffice />} />
          <Route path="/office/:id" element={<EditOffice />} />
          <Route path="/create-office" element={<OfficeForm />} />
          <Route path="booking-details/:id" element={<BookingsDetails />} />
          <Route path="/registration" element={<Registration />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;