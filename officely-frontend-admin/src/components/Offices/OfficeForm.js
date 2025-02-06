import React, { useState, useContext, useEffect } from 'react';
import Header from '../Header';
import '../../pages/Styles.css';
import { UserContext } from '../../context/UserContext';

const OfficeForm = () => {
  const { userEmail } = useContext(UserContext);
  const [selectedAmenities, setSelectedAmenities] = useState([]);
  const [amenities, setAmenities] = useState([]); //all amenities
    const [newAmenity, setNewAmenity] = useState('');

    const [error, setError] = useState(null);
  const [formData, setFormData] = useState({images: []});
    const [showAmenityInput, setShowAmenityInput] = useState(false);
    const [showImagePopup, setShowImagePopup] = useState(false);



    // Fetch the amenities data
  useEffect(() => {
    const fetchAmenities = async () => {
      try {
        const response = await fetch('https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/amenities', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': userEmail
          }
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setAmenities(data);
      } catch (error) {
        console.error('There was an error fetching the amenities!', error);
      }
    };

    fetchAmenities();
  }, []);

  //handling input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const { name, metricArea, floor, roomNumber, country, city, postalCode, address, price, images } = formData;

    const postalCodePattern = /^\d{2}-\d{3}$/;
    if (!postalCodePattern.test(postalCode)) {
        alert('Postal code must be in the format XX-XXX');
        return;
    }

    if (!name || !metricArea || !floor || !roomNumber || !country || !city || !address || !price || !selectedAmenities.length || !images) {
        alert('Please fill in all fields');
        console.log(JSON.stringify(formData));
        return;
    }

    const officeData = {
        name,
        metricArea: parseFloat(metricArea),
        floor: parseInt(floor),
        roomNumber: parseInt(roomNumber),
        country,
        city,
        postalCode,
        address,
        price: parseFloat(price),
        amenities: selectedAmenities.map(id => ({ id: id, name:"someName" })),
        images: Array.isArray(images) ? images.map(image => ({ id: parseInt(image.id), data:"someData" })) : [{ id: parseInt(images), data:"someData" }],
        x: 1,
        y: 1
    };

    const requestBody = {
        email: userEmail,
        office: officeData
    };

    console.log('Creating office with data:', requestBody);

    try {
        console.log('Sending POST request to https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices');
        const response = await fetch('https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': userEmail
            },
            body: JSON.stringify(requestBody)
        });

        console.log('Request body:', requestBody);
        console.log(JSON.stringify(requestBody));

        console.log('Response status:', response.status);
        const responseData = await response.json();
        console.log('Response data:', responseData);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        alert('Office created successfully');
    } catch (error) {
        console.error('There was an error creating the office!', error);
        setError(error.message);
    }
};

    const handleRemoveImage = async (imageId) => {
        try {
            // Send DELETE request to remove the image
            const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/images/${imageId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': userEmail
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to delete image with ID: ${imageId}`);
            }

            // Remove the image from local state after successful deletion
            setFormData((prevData) => ({
                ...prevData,
                images: prevData.images.filter((img) => img.id !== imageId)
            }));

            alert('Image removed successfully');
        } catch (error) {
            console.error('Error removing image:', error);
        }
    };

    const handleImageUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onloadend = async () => {
            const base64Data = reader.result.split(',')[1];
            const requestBody = { image: { data: base64Data } };

            try {
                const response = await fetch('https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/images', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': userEmail
                    },
                    body: JSON.stringify(requestBody)
                });

                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

                const responseData = await response.json();

                if (responseData && responseData.id) {
                    setFormData(prev => ({
                        ...prev,
                        images: [...(prev.images || []), { id: responseData.id, data: base64Data }]
                    }));
                }

                setShowImagePopup(false);
            } catch (error) {
                console.error('Error uploading image!', error);
            }
        };
    };


    const handleAddAmenity = async () => {
        if (!newAmenity.trim()) return;

        const requestBody = { amenity: { name: newAmenity } };
        try {
            const response = await fetch('https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/amenities', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': userEmail
                },
                body: JSON.stringify(requestBody)
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const newAmenityData = await response.json();
            setAmenities([...amenities, newAmenityData]);
            setNewAmenity('');
            setShowAmenityInput(false);
        } catch (error) {
            console.error('Error adding amenity!', error);
        }
    };

    const handleCheckboxChange = (event) => {
        const { value, checked } = event.target;
        const id = parseInt(value, 10);
        if (checked) {
            setSelectedAmenities([...selectedAmenities, id]);
        } else {
            setSelectedAmenities(selectedAmenities.filter((amenityId) => amenityId !== id));
        }
    };

  return (
    <div className="office-edit">
      <Header />
      <form className="office-edit-header" onSubmit={handleSubmit}>
        <h1>Add an office</h1>
        <p>Name</p>
        <input className='input' type="text" name="name" placeholder="Name" value={formData.name} onChange={handleChange} required />
        <p>Metric Area</p>
        <input className='input' type="number" name="metricArea" placeholder="Metric Area" value={formData.metricArea} onChange={handleChange} required />
        <p>Floor number</p>
        <input className='input' type="number" name="floor" placeholder="Floor" value={formData.floor} onChange={handleChange} required />
        <p>Room number</p>
        <input className='input' type="number" name="roomNumber" placeholder="Room Number" value={formData.roomNumber} onChange={handleChange} required />
        <p>Country</p>
        <input className='input' type="text" name="country" placeholder="Country" value={formData.country} onChange={handleChange} required />
        <p>City</p>
        <input className='input' type="text" name="city" placeholder="City" value={formData.city} onChange={handleChange} required />
        <p>Postal code [XX-XXX] </p>
        <input className='input' type="text" name="postalCode" placeholder="Postal Code" value={formData.postalCode} onChange={handleChange} required />
        <p>Address</p>
        <input className='input' type="text" name="address" placeholder="Address" value={formData.address} onChange={handleChange} required />
        <p>Price</p>
        <input className='input' type="number" name="price" placeholder="Price" value={formData.price} onChange={handleChange} required />
        <p>Amenities:</p>
        {amenities.map((amenity) => (
          <div className="amenities" key={amenity.id}>
            <label>
              <input
                type="checkbox"
                value={amenity.id}
                checked={selectedAmenities.includes(amenity.id)}
                onChange={handleCheckboxChange}
              />
              {amenity.name}
            </label>
          </div>
        ))}
      <button type="button" onClick={() => setShowAmenityInput(true)}>Add Amenity</button>
      {showAmenityInput && (
          <div>
              <input type="text" value={newAmenity} onChange={(e) => setNewAmenity(e.target.value)} placeholder="New amenity name" />
              <button type="button" onClick={handleAddAmenity}>Submit</button>
          </div>
      )}

          <p>Images</p>
          <ul>
              {formData.images.map((img) => (
                  <li key={img.id}>
                      <img
                          src={`data:image/png;base64,${img.data}`}
                          alt={`Image ID: ${img.id}`}
                          style={{ maxWidth: '100px', maxHeight: '100px', objectFit: 'contain' }}
                      />
                      <button
                          type="button"
                          onClick={() => handleRemoveImage(img.id)} // Call the function to remove the image
                      >
                          Remove
                      </button>
                  </li>
              ))}
          </ul>

          <button type="button" onClick={() => setShowImagePopup(true)}>Upload Image</button>
          {showImagePopup && (
              <div>
                  <input type="file" accept="image/*" onChange={handleImageUpload} />
              </div>
          )}
          <button className='button' type="submit">Create Office</button>
      </form>
    </div>
  );
};

export default OfficeForm;