import React, {useContext, useEffect, useState} from 'react';
import OfficesListItem from './OfficesListItem';
import '../../pages/Styles.css';
import {UserContext} from "../../context/UserContext";

const OfficesList = ({ searchTerm, onEditOffice, onAddOffice }) => {
  const [officesData, setOfficesData] = useState([]);
  const { userEmail } = useContext(UserContext);


  const fetchOffices = async () => {
    try {
      const response = await fetch("https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/offices", {
        method: "GET",
        headers: {
          "Authorization": userEmail
        }
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      console.log('Fetched data:', data);
      setOfficesData(data);
    } catch (error) {
      console.error("Error fetching offices:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      console.log(`Sending DELETE request to https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices?id=${id}`);
      const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices?id=${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': userEmail
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      alert('Office deleted successfully');
      await fetchOffices(); // Refresh the list of offices
    } catch (error) {
      console.error('There was an error deleting the office!', error);
    }
  };

  useEffect(() => {
    fetchOffices();
  }, []);

  const filteredData = officesData.filter((el) => {
    if (searchTerm === '') {
      return el;
    } else {
      return el.name.toLowerCase().includes(searchTerm.toLowerCase()); // convert searchTerm to lowercase
    }
  });

  return (
    <div className='list-header'>
      <button className='button-add-office' onClick={onAddOffice}>Add an office</button>
      <div className='list-container'>
        {filteredData.map((o) => (
          <OfficesListItem
            key={o.id}
            office={o}
            onDeleteOffice={() => handleDelete(o.id)}
            editHandler={onEditOffice}
          />
        ))}
      </div>
    </div>
  );
};

export default OfficesList;
