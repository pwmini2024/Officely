import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Alert,
  RefreshControl,
} from "react-native";
import { useAuth } from "../context/AuthContext";
import { SafeAreaView } from "react-native-safe-area-context";

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

export default function ParkingList({ route, navigation }) {
  const { officeId, startDate, endDate } = route.params;
  const { email } = useAuth();
  const [parkingSpots, setParkingSpots] = useState([]);
  const [refreshing, setRefreshing] = useState(false);
  const [name, setName] = useState('');
  const [surname, setSurname] = useState('');
  const [userID, setUserID] = useState('');
  const [username, setUsername] = useState('');

  useEffect(() => {
    fetchParkingSpots();
    fetchUserInfo();
  }, []);

  useEffect(() => {
    setUsername(`${name}_${surname}_${userID}`);
  }, [name, surname, userID]);

  const fetchParkingSpots = async () => {
    try {
      const response = await fetch(
        `${AZURE_ENDPOINT}/parking?officeId=${officeId}&dateStart=${startDate}&dateEnd=${endDate}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: email,
          },
        }
      );
      if (response.ok) {
        const data = await response.json();
        setParkingSpots(data);
      } else {
        Alert.alert("Error", "Failed to fetch parking spots.");
      }
    } catch (error) {
      Alert.alert("Error", "Failed to fetch parking spots.");
    } finally {
      setRefreshing(false);
    }
  };

  const fetchUserInfo = async () => {
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/users`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': email,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setName(data.name);
        setSurname(data.surname);
      } else {
        Alert.alert('Error', 'Failed to fetch user information.');
      }
    } catch (error) {
      Alert.alert('Error', 'Failed to fetch user information.');
    }
  };

  const handleBookParking = async (parkingId) => {
    const requestBody = {
      parkingAreaId: parkingId,
      startTime: startDate,
      endTime: endDate,
    };

    try {
      const response = await fetch(
        `${AZURE_ENDPOINT}/parking/reservation`,
        {
          method: "POST",
          headers: {
            Authorization: email,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );
      if (response.ok) {
        const responseData = await response.json();
        setUserID(responseData.userId);
        setUsername(`${name}_${surname}_${responseData.userId}`);
        Alert.alert("Success", `Parking spot reserved successfully. You can access your parking spots and modify them through the Parkly app with the following username: ${username}`, [
          {
            text: "OK",
          },
        ]);
      } else {
        Alert.alert("Error", "There are no parking spaces left.");
      }
    } catch (error) {
      Alert.alert("Error", "Failed to reserve parking spot.");
      console.error(error);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    fetchParkingSpots();
  };

  const renderParkingItem = ({ item }) => (
    <View style={styles.parkingItem}>
      <Text style={styles.parkingText}>{item.name}</Text>
      <TouchableOpacity
        style={styles.bookButton}
        onPress={() => handleBookParking(item.id)}
      >
        <Text style={styles.bookButtonText}>Book</Text>
      </TouchableOpacity>
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.container}>
        
        <Text style={styles.header}>Available Parking Spots</Text>
        <FlatList
          data={parkingSpots}
          renderItem={renderParkingItem}
          keyExtractor={(item) => item.id.toString()}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        />
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backButton}>
          <Text style={styles.backButtonText}>Return Back</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  backButton: {
    marginBottom: 20,
    padding: 10,
    backgroundColor: "#ddd",
    borderRadius: 5,
    alignItems: "center",
  },
  backButtonText: {
    fontSize: 16,
    color: "#000",
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
  },
  parkingItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: "#ccc",
  },
  parkingText: {
    fontSize: 18,
  },
  bookButton: {
    backgroundColor: "#4CAF50",
    padding: 10,
    borderRadius: 5,
  },
  bookButtonText: {
    color: "#fff",
    fontSize: 16,
  },
});