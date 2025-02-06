import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../Header';
import '../../pages/Styles.css';
import { UserContext } from '../../context/UserContext';
import ImageCarousel from '../ImageCarousel';

const EditOffice = () => {
  const navigate = useNavigate();
  const { userEmail } = useContext(UserContext);
  const { id } = useParams();
  const [isEditing, setIsEditing] = useState(false); // default is view mode
  const [office, setOffice] = useState(null);
  const [amenities, setAmenities] = useState([]);
  const [newAmenity, setNewAmenity] = useState('');

  const [selectedAmenities, setSelectedAmenities] = useState([]);
  const [showAmenityInput, setShowAmenityInput] = useState(false);
  const [showImagePopup, setShowImagePopup] = useState(false);


  const [formData, setFormData] = useState({
    name: '',
    metricArea: '',
    floor: '',
    roomNumber: '',
    country: '',
    city: '',
    postalCode: '',
    address: '',
    price: '',
    amenities: [],
    images: []
  });
  const [error, setError] = useState(null);
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

  // GET - Fetch the offices data by ID and populate the form
  useEffect(() => {
    const fetchOffice = async () => {
      console.log(`Fetching office with ID: ${id}`);
      try {
        const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/offices/${id}`, {
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
        setFormData(data);
        setOffice(data);
        setSelectedAmenities(data.amenities.map(amenity => amenity.id));
      } catch (error) {
        setError(error.message);
        console.error('Error fetching office:', error);
      }
    };

    fetchOffice();
  }, [id]);

  useEffect(() => {
    const fetchImages = async () => {
      if (!formData.images || formData.images.length === 0) return;

      try {
        const imagePromises = formData.images.map(async (image) => {
          console.log('Fetching from address https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/images/', image.id);
          const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/images/${image.id}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': userEmail
            }
          });

          if (!response.ok) {
            throw new Error(`Failed to fetch image with ID: ${image.id}`);
          }
          return response.json();
        });

        const imagesData = await Promise.all(imagePromises);
        setFormData((prevData) => ({
          ...prevData,
          images: imagesData, // Store image objects instead of just IDs
        }));
      } catch (error) {
        console.error('Error fetching images:', error);
      }
    };

    fetchImages();
  }, [formData.images.length]); // Ensure userEmail is included in dependencies if it's dynamic

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

  // DELETE - Delete the office by ID
  const handleDelete = async () => {
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
    } catch (error) {
      console.error('There was an error deleting the office!', error);
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


  // PUT - Update the office by ID
  const handleSubmit = async (e) => {
    e.preventDefault();
    const { name, metricArea, floor, roomNumber, country, city, postalCode, address, price, amenities, images } = formData;
  
    const postalCodePattern = /^\d{2}-\d{3}$/;
    if (!postalCodePattern.test(postalCode)) {
      alert('Postal code must be in the format XX-XXX');
      return;
    }

    if (!name) {
      alert('Please fill in the Name field');
      console.log('Missing field: Name');
      return;
    }
    if (!metricArea) {
      alert('Please fill in the Metric Area field');
      console.log('Missing field: Metric Area');
      return;
    }
    if (!floor) {
      alert('Please fill in the Floor field');
      console.log('Missing field: Floor');
      return;
    }
    if (!roomNumber) {
      alert('Please fill in the Room Number field');
      console.log('Missing field: Room Number');
      return;
    }
    if (!country) {
      alert('Please fill in the Country field');
      console.log('Missing field: Country');
      return;
    }
    if (!city) {
      alert('Please fill in the City field');
      console.log('Missing field: City');
      return;
    }
    if (!address) {
      alert('Please fill in the Address field');
      console.log('Missing field: Address');
      return;
    }
    if (!price) {
      alert('Please fill in the Price field');
      console.log('Missing field: Price');
      return;
    }
    if (!selectedAmenities.length) {
      alert('Please select at least one Amenity');
      console.log('Missing field: Amenities');
      return;
    }
    if (!images) {
      alert('Please fill in the Images field');
      console.log('Missing field: Images');
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
  
    console.log('Updating office with data:', requestBody);
  
    try {
      console.log(`Sending PUT request to https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices?id=${id}`);
      const response = await fetch(`https://officely-epdmeqcbe7c0a8gq.polandcentral-01.azurewebsites.net/admin/offices?id=${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': userEmail
        },
        body: JSON.stringify(requestBody)
      });
  
      console.log("Request body:", JSON.stringify(requestBody));
  
      console.log('Response status:', response.status);
      const responseData = await response.json();
      console.log('Response data:', responseData);
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      alert('Office updated successfully');
      setOffice(formData);
      setIsEditing(false);
    } catch (error) {
      console.error('There was an error updating the office!', error);
    }
  };

  const toggleEditing = () => {
    setIsEditing(!isEditing);
    if (isEditing) {
      setFormData(office);
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleCancel = () => {
    setFormData(office);
    toggleEditing();
  };
  

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="office-edit">
      <Header />
      {isEditing ? (
        <div className="office-edit-header">
          <h1>Edit office </h1>
          <p>Office ID: {id}</p>
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
          <button className='button-edit' type="button" onClick={() => setShowAmenityInput(true)}>Add Amenity</button>
          {showAmenityInput && (
              <div>
                <input type="text" value={newAmenity} onChange={(e) => setNewAmenity(e.target.value)} placeholder="New amenity name" />
                <button className='button' type="button" onClick={handleAddAmenity}>Submit</button>
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
                  className='button-edit' 
                      type="button"
                      onClick={() => handleRemoveImage(img.id)} // Call the function to remove the image
                  >
                    Remove
                  </button>
                </li>
            ))}
          </ul>
          <button className='button-edit' type="button" onClick={() => setShowImagePopup(true)}>Upload Image</button>
          {showImagePopup && (
              <div>
                <input type="file" accept="image/*" onChange={handleImageUpload} />
              </div>
          )}
          <button className="button" onClick={handleCancel}>Cancel</button>
          <button className="button" onClick={handleSubmit}>Save</button>
        </div>
      ) : (
        <div>
          <div className="office-edit-header">
            <button className="back-button" onClick={() => navigate('/offices')}>
              ←
            </button>
            <h1>Office Details</h1>
            <p>Office ID: {id}</p>
            <p>Name: {formData.name}</p>
            <p>Metric Area: {formData.metricArea} m²</p>
            <p>Floor: {formData.floor}</p>
            <p>Room Number: {formData.roomNumber}</p>
            <p>Country: {formData.country}</p>
            <p>City: {formData.city}</p>
            <p>Postal Code: {formData.postalCode}</p>
            <p>Address: {formData.address}</p>
            <p>Price: {formData.price}</p>
            <p>Amenities: {formData.amenities.map(amenity => amenity.name).join(', ')}</p>
            <p>Images: </p>
            <ImageCarousel images={formData.images} />
            <button className="button" onClick={handleDelete}>Delete</button>
            <button className="button" onClick={toggleEditing}>Edit</button>
          </div>
        </div>
      )}

    </div>
  );
};

export default EditOffice;


{/* <ul>
              {formData.images.map((img) => (
                  <li key={img.id}>
                    <img src={`data:image/png;base64,${img.data}`}
                         alt={`Image ID: ${img.id}`}
                         style={{ maxWidth: '100px', maxHeight: '100px', objectFit: 'contain' }}
                    />
                  </li>
              ))}
            </ul> */}