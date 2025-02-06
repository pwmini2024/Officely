import React, { useState, useEffect } from 'react';
import { View, Text, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../../context/AuthContext';

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

interface ListingItemProps {
  id: string;
  images: { id: number; data: string | null }[];
  name: string;
  address: string;
  city: string;
  country: string;
  postalCode: string;
  floor: number;
  roomNumber: number;
  metricArea: number;
  price: number;
  amenities: { id: number; name: string }[];
  onViewDetails: () => void;
}

const ListingItem: React.FC<ListingItemProps> = ({
  id,
  images,
  name,
  address,
  city,
  country,
  postalCode,
  floor,
  roomNumber,
  metricArea,
  price,
  amenities,
  onViewDetails,
}) => {
  const { email } = useAuth();
  const [imageSource, setImageSource] = useState<string | null>(null);

  useEffect(() => {
    if (images && images.length > 0) {
      fetchImageById(images[0].id).then((data) => {
        if (data && data.data) {
          setImageSource(`data:image/jpeg;base64,${data.data}`);
        }
      });
    }
  }, [images]);

  const fetchImageById = async (id: number) => {
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/images/${id}`, {
        method: 'GET',
        headers: {
          'Authorization': email,
          'Content-Type': 'application/json'
        }
      });
      if (response.ok) {
        const data = await response.json();
        return data;
      } else {
        console.error('Error fetching image:', response.status);
        return null;
      }
    } catch (error) {
      console.error('Error fetching image:', error);
      return null;
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.imageContainer}>
          <Image source={{ uri: imageSource }} style={styles.image} />
      </View>
      <View style={styles.details}>
        <Text style={styles.title}>{name}</Text>
        <Text style={styles.text}>{address}, {city}, {country}, {postalCode}</Text>
        <Text style={styles.text}>Floor: {floor}</Text>
        <Text style={styles.text}>Room Number: {roomNumber}</Text>
        <Text style={styles.text}>Area: {metricArea} m²</Text>
      </View>
      <View style={styles.actions}>
        <Text style={styles.price}>{price} PLN / 1 Day(s)</Text>
      </View>
      <View style={styles.actions}>
        <TouchableOpacity style={styles.button} onPress={onViewDetails}>
          <Text style={styles.buttonText}>View Details</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 15,
    margin: 10,
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 8,
    elevation: 2,
  },
  imageContainer: {
    marginBottom: 10,
  },
  image: {
    width: '100%',
    height: 200,
    borderRadius: 8,
  },
  details: {
    marginBottom: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  text: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 10,
  },
  price: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'rgba(48, 91, 5, 0.8)',
  },
  button: {
    backgroundColor: '#5B5F97',
    padding: 10,
    borderRadius: 5,
  },
  buttonText: {
    color: '#fff',
    fontSize: 14,
  },
});

export default ListingItem;