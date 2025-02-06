import React, {useState, useEffect, useContext} from 'react';
import { useNavigate } from 'react-router-dom';
import './OfficesListItem.css';
import {UserContext} from "../../context/UserContext";

const OfficesListItem = ({ office, index, onDeleteOffice }) => {
    const navigate = useNavigate();
    const [imageUrl, setImageUrl] = useState('https://via.placeholder.com/150');
    const { userEmail } = useContext(UserContext);

    useEffect(() => {
        const fetchImage = async () => {
            if (office.images.length === 0) {
                setImageUrl('https://via.placeholder.com/150');
                return;
            }

            try {
                const imageId = office.images[0].id;
                console.log('Fetching from address https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/images/', imageId);
                const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/images/${imageId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': userEmail
                    }
                });

                if (!response.ok) {
                    throw new Error(`Failed to fetch image with ID: ${imageId}`);
                }

                const imageData = await response.json();
                setImageUrl(`data:image/png;base64,${imageData.data}`);
            } catch (error) {
                console.error('Error fetching image:', error);
                setImageUrl('https://via.placeholder.com/150');
            }
        };

        fetchImage();
    }, [office.images]);

    const handleDelete = () => {
        onDeleteOffice(index);
    };

    const handleEdit = () => {
        navigate(`/edit-office/${office.id}`);
    };

    return (
        <div className="item-container">
            <img src={imageUrl} alt="Office" className="avatar" />
            <div className="office-info">
                <p className="name">{office.name}</p>
                <p className="location">{office.address}, {office.postalCode}, {office.city}</p>
            </div>
            <div className="action-buttons">
                <button className="edit-button" onClick={handleEdit}>View details</button>
                <button className="delete-button" onClick={handleDelete}>Delete</button>
            </div>
        </div>
    );
};

export default OfficesListItem;