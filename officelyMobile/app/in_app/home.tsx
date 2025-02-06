import React, { useState, useEffect } from 'react';
import { StyleSheet, ScrollView, View, Text, TouchableOpacity, TextInput, Alert } from 'react-native';
import ListingList from './listing_items/listing_list';
import DateTimePickerModal from 'react-native-modal-datetime-picker';
import { useAuth } from '../context/AuthContext';
import { RefreshControl } from 'react-native';

const GEOCODING_API_KEY = process.env.EXPO_PUBLIC_API;
const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

export default function Home({ navigation }) {
  const [search, setSearch] = useState('');
  const [isStartDatePickerVisible, setStartDatePickerVisibility] = useState(false);
  const [isEndDatePickerVisible, setEndDatePickerVisibility] = useState(false);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [refreshing, setRefreshing] = useState(false);
  const [offices, setOffices] = useState([]);
  const { email, setEmail } = useAuth();

  const onRefresh = () => {
    setRefreshing(true);
    fetchOffices();
  };

  useEffect(() => {
    fetchOffices();
  }, []);

  const fetchCoordinates = async (location) => {
    try {
      const response = await fetch(`https://api.opencagedata.com/geocode/v1/json?q=${encodeURIComponent(location)}&key=${GEOCODING_API_KEY}`);
      if (response.ok) {
        const data = await response.json();
        if (data.results && data.results.length > 0) {
          const { lat, lng } = data.results[0].geometry;
          return { lat, lng };
        } else {
          Alert.alert('Error', 'No results found for the specified location.');
          return null;
        }
      } else {
        Alert.alert('Error', 'Failed to fetch coordinates.');
        return null;
      }
    } catch (error) {
      Alert.alert('Error', 'Failed to fetch coordinates.');
      return null;
    }
  };

  const fetchOffices = async (searchQuery = '') => {
    try {
      let url = `${AZURE_ENDPOINT}/offices`;

      if (searchQuery) {
        const coordinates = await fetchCoordinates(searchQuery);
        if (coordinates) {
          url += `?x=${coordinates.lng}&y=${coordinates.lat}&distance=20`;
        } else {
          return;
        }
      }
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': email,
          'Content-Type': 'application/json'
        }
      });
      if (response.ok) {
        const data = await response.json();
        setOffices(data);
      } else {
        const errorData = await response.json();
        Alert.alert('Error', `Failed to fetch office listings: There are no offices in the search area try another one`);
      }
    } catch (error) {
      Alert.alert('Error', `Failed to fetch office listings`);
    } finally {
      setRefreshing(false);
    }
  };

  const handleSearch = () => {
    fetchOffices(search);
  };

  const showStartDatePicker = () => {
    setStartDatePickerVisibility(true);
  };

  const hideStartDatePicker = () => {
    setStartDatePickerVisibility(false);
  };

  const handleStartConfirm = (date: Date) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (date < today) {
      Alert.alert('Invalid Date', 'Start date cannot be earlier than today.');
    } else {
      setStartDate(date.toISOString().split('T')[0]);
      hideStartDatePicker();
    }
  };

  const showEndDatePicker = () => {
    setEndDatePickerVisibility(true);
  };

  const hideEndDatePicker = () => {
    setEndDatePickerVisibility(false);
  };

  const handleEndConfirm = (date: Date) => {
    const start = new Date(startDate);
    start.setHours(0, 0, 0, 0); 
    if (date < start) {
      Alert.alert('Invalid Date', 'End date cannot be earlier than start date.');
    } else {
      setEndDate(date.toISOString().split('T')[0]);
      hideEndDatePicker();
    }
  };

  const handleViewDetails = (officeInfo) => {
    navigation.navigate('OfficeDetails', { officeInfo, startDate, endDate });
  };



  return (
    <View style={{ flex: 1 }}>
      <View style={styles.searchBarContainer}>
        <TextInput
          style={styles.searchBarInput}
          placeholder="Search"
          value={search}
          onChangeText={setSearch}
        />
        <TouchableOpacity onPress={handleSearch} style={styles.searchButton}>
          <Text style={styles.searchButtonText}>Search</Text>
        </TouchableOpacity>
      </View>
      <View style={styles.datePickerContainer}>
        <TouchableOpacity onPress={showStartDatePicker} style={styles.datePickerButton}>
          <Text style={styles.datePickerText}>{startDate || 'Select Start Date'}</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={showEndDatePicker} style={styles.datePickerButton}>
          <Text style={styles.datePickerText}>{endDate || 'Select End Date'}</Text>
        </TouchableOpacity>
      </View>
      <DateTimePickerModal
        isVisible={isStartDatePickerVisible}
        mode="date"
        onConfirm={handleStartConfirm}
        onCancel={hideStartDatePicker}
        minimumDate={new Date()} 
      />
      <DateTimePickerModal
        isVisible={isEndDatePickerVisible}
        mode="date"
        onConfirm={handleEndConfirm}
        onCancel={hideEndDatePicker}
        minimumDate={startDate ? new Date(startDate) : new Date()} // Ensure end date is not earlier than start date
      />
      <ScrollView contentContainerStyle={styles.container}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }>
        <ListingList listings={offices} onViewDetails={handleViewDetails} />
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
  searchBarInput: {
    flex: 1,
    height: 40,
    color: '#000',
  },
  searchButton: {
    backgroundColor: '#5B5F97',
    padding: 10,
    borderRadius: 5,
    marginLeft: 10,
  },
  searchButtonText: {
    color: '#fff',
    fontSize: 14,
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
  logoutContainer: {
    alignItems: 'flex-end',
    margin: 20,
  },
  logoutButton: {
    backgroundColor: '#FF6347',
    padding: 10,
    borderRadius: 5,
  },
  logoutButtonText: {
    color: '#fff',
    fontSize: 14,
  },
});