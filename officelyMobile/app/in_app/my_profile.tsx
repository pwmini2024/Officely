import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  RefreshControl,
} from "react-native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { useAuth } from "../context/AuthContext";

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

export default function MyProfile() {
  const { email } = useAuth();
  interface ProfileDetails {
    name?: string;
    surname?: string;
    email?: string;
    birthDate?: string;
    phoneNumber?: string;
  }

  const [profileDetails, setProfileDetails] = useState<ProfileDetails>({});
  const [isEditing, setIsEditing] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/users`, {
        method: "GET",
        headers: {
          Authorization: email,
          "Content-Type": "application/json",
        },
      });
      if (response.ok) {
        const data = await response.json();
        setProfileDetails(data);
      } else {
        const errorData = await response.json();
        console.error("Error fetching profile details:", errorData);
        Alert.alert(
          "Error",
          `Failed to fetch profile details: ${errorData.message}`
        );
      }
    } catch (error) {
      console.error("Error fetching profile details:", error);
      Alert.alert("Error", `Failed to fetch profile details`);
    } finally {
      setRefreshing(false);
    }
  };

  const handleInputChange = (field, value) => {
    setProfileDetails({ ...profileDetails, [field]: value });
  };

  const toggleEditing = async () => {
    const requestBody = {
      user: {
        email: profileDetails.email,
        name: profileDetails.name,
        surname: profileDetails.surname,
        birthDate: profileDetails.birthDate,
        phoneNumber: profileDetails.phoneNumber,
      },
    };
    if (isEditing) {
      // Save changes
      try {
        const response = await fetch(`${AZURE_ENDPOINT}/users`, {
          method: "PUT",
          headers: {
            Authorization: email,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        });

        if (response.ok) {
          const data = await response.json();
          Alert.alert("Success", "Profile updated successfully");
          setProfileDetails(data);
        } else {
          const errorData = await response.json();
          console.error("Error Response:", errorData);
          console.error("Status Code:", response.status);
          Alert.alert(
            "Error",
            errorData.message || "Failed to update profile"
          );
        }
      } catch (error) {
        console.error("Catch Error:", error);
        Alert.alert("Error", "Failed to update profile");
      }
    }
    setIsEditing(!isEditing);
  };

  const onRefresh = () => {
    setRefreshing(true);
    fetchProfile();
  };

  const showDatePicker = () => {
    setDatePickerVisibility(true);
  };

  const hideDatePicker = () => {
    setDatePickerVisibility(false);
  };

  const handleConfirm = (date) => {
    setProfileDetails({
      ...profileDetails,
      birthDate: date.toISOString().split("T")[0],
    });
    hideDatePicker();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>My Profile</Text>
      <ScrollView
        contentContainerStyle={styles.content}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      >
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Name</Text>
          {isEditing ? (
            <TextInput
              style={styles.input}
              value={profileDetails.name || ""}
              onChangeText={(text) => handleInputChange("name", text)}
            />
          ) : (
            <Text style={styles.textValue}>{profileDetails.name}</Text>
          )}
        </View>

        {/* Surname Field */}
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Surname</Text>
          {isEditing ? (
            <TextInput
              style={styles.input}
              value={profileDetails.surname || ""}
              onChangeText={(text) => handleInputChange("surname", text)}
            />
          ) : (
            <Text style={styles.textValue}>{profileDetails.surname}</Text>
          )}
        </View>

        {/* Email Field */}
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Email</Text>
          <Text style={styles.textValue}>{profileDetails.email}</Text>
        </View>

        {/* Date of Birth Field */}
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Date of birth</Text>
          {isEditing ? (
            <TouchableOpacity onPress={showDatePicker} style={styles.input}>
              <Text>{profileDetails.birthDate || "Select Date"}</Text>
            </TouchableOpacity>
          ) : (
            <Text style={styles.textValue}>{profileDetails.birthDate}</Text>
          )}
          <DateTimePickerModal
            isVisible={isDatePickerVisible}
            mode="date"
            onConfirm={handleConfirm}
            onCancel={hideDatePicker}
            maximumDate={new Date()}
          />
        </View>

        {/* Phone Number Field */}
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Phone number</Text>
          {isEditing ? (
            <TextInput
              style={styles.input}
              value={profileDetails.phoneNumber || ""}
              onChangeText={(text) => handleInputChange("phoneNumber", text)}
            />
          ) : (
            <Text style={styles.textValue}>{profileDetails.phoneNumber}</Text>
          )}
        </View>

        {/* Edit Profile Button */}
        <TouchableOpacity style={styles.button} onPress={toggleEditing}>
          <Text style={styles.buttonText}>
            {isEditing ? "Save Changes" : "Edit Profile"}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f8f9fa",
  },
  header: {
    fontSize: 20,
    fontWeight: "bold",
    textAlign: "center",
    marginVertical: 20,
    color: "#333",
  },
  content: {
    paddingHorizontal: 20,
  },
  profilePictureContainer: {
    alignItems: "center",
    marginBottom: 20,
  },
  profilePicture: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: "#ccc",
  },
  profilePictureText: {
    marginTop: 10,
    fontSize: 14,
    color: "#666",
  },
  inputContainer: {
    marginBottom: 15,
  },
  label: {
    fontSize: 14,
    color: "#333",
    marginBottom: 5,
  },
  input: {
    backgroundColor: "#fff",
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 5,
    padding: 10,
    fontSize: 14,
    color: "#333",
  },
  textValue: {
    fontSize: 14,
    color: "#555",
    padding: 10,
    backgroundColor: "#f0f0f0",
    borderRadius: 5,
  },
  button: {
    backgroundColor: "#333",
    paddingVertical: 12,
    borderRadius: 5,
    alignItems: "center",
    marginTop: 20,
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
  },
});