import React, { useState, useEffect } from 'react';
import { StyleSheet, ScrollView, View, Text, TouchableOpacity, TextInput, Alert } from 'react-native';
import ListingList from './listing_items/listing_list_my_bookings';
import { useAuth } from '../context/AuthContext';
import { RefreshControl } from 'react-native';

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

export default function MyBookings({ navigation }) {
  const [refreshing, setRefreshing] = useState(false);
  const [bookings, setBookings] = useState([]);
  const { email } = useAuth();

  const onRefresh = () => {
    setRefreshing(true);
    fetchBookings();
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/reservations`, {
        method: 'GET',
        headers: {
          'Authorization': email,
          'Content-Type': 'application/json'
        }
      });
      if (response.ok) {
        const data = await response.json();
        setBookings(data);
      } else {
        const errorData = await response.json();
        Alert.alert('Error', `Failed to fetch office listings: ${errorData.message}`);
      }
    } catch (error) {
      Alert.alert('Error', `Failed to fetch office listings`);
    }
    finally
    {
      setRefreshing(false);
    }
  };

  return (
    <View style={{ flex: 1 }}>
      <ScrollView contentContainerStyle={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }>
        <ListingList bookings={bookings} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
  searchBarContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#e0e0e0',
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 5,
    marginVertical: 10,
    marginHorizontal: 20,
  },
  searchIcon: {
    marginRight: 10,
  },
  searchBarInput: {
    flex: 1,
    height: 40,
    color: '#000',
  },
  datePickerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginHorizontal: 20,
    padding: 10,
  },
  datePickerButton: {
    backgroundColor: '#5B5F97',
    width: '48%',
    padding: 10,
    borderRadius: 5,
  },
  datePickerText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 14,
  },
});