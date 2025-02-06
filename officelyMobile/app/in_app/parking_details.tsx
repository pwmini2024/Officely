import React, { useState, useEffect } from "react";
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Image, Dimensions, Modal, Alert, Platform } from "react-native";
import DateTimePicker from '@react-native-community/datetimepicker';
import { useAuth } from '../context/AuthContext';

const { width: viewportWidth } = Dimensions.get('window');

const OfficeDetails = ({ route, navigation }) => {
  const { officeInfo, startDate: initialStartDate, endDate: initialEndDate } = route.params;
  const { email } = useAuth();
  const [images, setImages] = useState([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [startDate, setStartDate] = useState(isNaN((new Date(initialStartDate)).getTime()) ? new Date() : new Date(initialStartDate));
  const [endDate, setEndDate] = useState(isNaN((new Date(initialEndDate)).getTime()) ? new Date((new Date).getTime() + 24 * 60 * 60 * 1000) : new Date(initialEndDate));
  const [showStartDatePicker, setShowStartDatePicker] = useState(false);
  const [showEndDatePicker, setShowEndDatePicker] = useState(false);


  useEffect(() => {
    if (officeInfo && officeInfo.images && officeInfo.images.length > 0) {
      const fetchImages = async () => {
        const imagePromises = officeInfo.images.map(image => fetchImageById(image.id));
        const imageData = await Promise.all(imagePromises);
        setImages(imageData.filter(data => data && data.data).map(data => `data:image/jpeg;base64,${data.data}`));
      };
      fetchImages();
    }
  }, [officeInfo]);

  const fetchImageById = async (id) => {
    try {
      const response = await fetch(`http://10.0.2.2:8080/images/${id}`, {
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

  const reserveOffice = async () => {
    const requestBody = {
      email: email,
      reservation: {
        startTime: startDate.toISOString().split('T')[0],
        endTime: endDate.toISOString().split('T')[0],
        paymentType: "CASH",
        comments: "Reservation 8"
      }
    };

    try {
      const response = await fetch(`http://10.0.2.2:8080/reservations/office/${officeInfo.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': email
        },
        body: JSON.stringify(requestBody)
      });

      if (response.ok) {
        Alert.alert('Success', 'Reservation created successfully');
      } else {
        const errorData = await response.json();
        console.error('Error Response:', errorData);
        Alert.alert('Error', errorData.message || 'Failed to create reservation, Choose other dates');
      }
    } catch (error) {
      console.error('Catch Error:', error);
      Alert.alert('Error', 'Failed to create reservation, Choose other dates');
    }
  };

  const handleBookPress = () => {
    setModalVisible(true);
  };

  const handleReservePress = () => {
    setModalVisible(false);
    reserveOffice();
  };

  const onStartDateChange = (event, selectedDate) => {
    const currentDate = selectedDate || startDate;
    setShowStartDatePicker(Platform.OS === 'ios');
    setStartDate(currentDate);
  };

  const onEndDateChange = (event, selectedDate) => {
    const currentDate = selectedDate || endDate;
    setShowEndDatePicker(Platform.OS === 'ios');
    setEndDate(currentDate);
  };

  const calculateDays = (start, end) => {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const timeDiff = endDate.getTime() - startDate.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
    return daysDiff;
  };

  const days = calculateDays(startDate, endDate);
  const totalPrice = officeInfo.price * days;

  if (!officeInfo) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>No office information available.</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.backArrow}>{"<"}</Text>
        </TouchableOpacity>
        <Text style={styles.headerText}>User App - Office Details</Text>
      </View>
      {images.length > 0 ? (
        <ScrollView horizontal pagingEnabled showsHorizontalScrollIndicator={false}>
          {images.map((image, index) => (
            <Image key={index} source={{ uri: image }} style={styles.image} />
          ))}
        </ScrollView>
      ) : (
        <View style={styles.imagePlaceholder}>
          <Text>No Image Available</Text>
        </View>
      )}
      <View style={styles.content}>
        <Text style={styles.officeTitle}>{officeInfo.name}</Text>
        <Text style={styles.location}>Location: {officeInfo.address}, {officeInfo.city}, {officeInfo.country}, {officeInfo.postalCode}</Text>
        <Text style={styles.location}>Floor: {officeInfo.floor}</Text>
        <Text style={styles.location}>Room Number: {officeInfo.roomNumber}</Text>
        <Text style={styles.location}>Area: {officeInfo.metricArea} m²</Text>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Amenities</Text>
          <Text style={styles.sectionContent}>
            {officeInfo.amenities && officeInfo.amenities.length > 0 ? (
              officeInfo.amenities.map((amenity, index) => (
                <Text key={index}>• {amenity.name}{"\n"}</Text>
              ))
            ) : (
              <Text>No amenities available.</Text>
            )}
          </Text>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Payment Options</Text>
          <Text style={styles.sectionContent}>
            {officeInfo.paymentOptions && officeInfo.paymentOptions.length > 0 ? (
              officeInfo.paymentOptions.map((option, index) => (
                <Text key={index}>• {option}{"\n"}</Text>
              ))
            ) : (
              <Text>No payment options available.</Text>
            )}
          </Text>
        </View>

        <Text style={styles.price}>{totalPrice} / {days} Day(s)</Text>

        <View style={styles.datePickerContainer}>
          <TouchableOpacity onPress={() => setShowStartDatePicker(true)} style={styles.datePickerButton}>
            <Text style={styles.datePickerText}>Start Date: {startDate.toDateString()}</Text>
          </TouchableOpacity>
          {showStartDatePicker && (
            <DateTimePicker
              value={startDate}
              mode="date"
              display="default"
              onChange={onStartDateChange}
            />
          )}
          <TouchableOpacity onPress={() => setShowEndDatePicker(true)} style={styles.datePickerButton}>
            <Text style={styles.datePickerText}>End Date: {endDate.toDateString()}</Text>
          </TouchableOpacity>
          {showEndDatePicker && (
            <DateTimePicker
              value={endDate}
              mode="date"
              display="default"
              onChange={onEndDateChange}
            />
          )}
        </View>

        <TouchableOpacity style={styles.bookButton} onPress={handleBookPress}>
          <Text style={styles.bookButtonText}>Book</Text>
        </TouchableOpacity>
      </View>

      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={() => {
          setModalVisible(!modalVisible);
        }}
      >
        <View style={styles.modalContainer}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Choose a Payment Style</Text>
            <TouchableOpacity style={styles.modalButton} onPress={handleReservePress}>
              <Text style={styles.modalButtonText}>Cash</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.modalCloseButton} onPress={() => setModalVisible(false)}>
              <Text style={styles.modalCloseButtonText}>Close</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    padding: 16,
    backgroundColor: "#fff",
  },
  backArrow: {
    fontSize: 18,
    marginRight: 8,
  },
  headerText: {
    fontSize: 16,
    fontWeight: "bold",
  },
  imagePlaceholder: {
    height: 200,
    backgroundColor: "#E0E0E0",
    justifyContent: "center",
    alignItems: "center",
  },
  image: {
    width: viewportWidth,
    height: 200,
  },
  content: {
    padding: 16,
  },
  officeTitle: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 8,
  },
  location: {
    fontSize: 16,
    color: "#555",
    marginBottom: 16,
  },
  section: {
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  sectionContent: {
    fontSize: 16,
    color: "#555",
  },
  price: {
    fontSize: 20,
    fontWeight: "bold",
    marginVertical: 16,
    textAlign: "center",
  },
  datePickerContainer: {
    marginBottom: 16,
  },
  datePickerButton: {
    backgroundColor: "#ddd",
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
    alignItems: "center",
  },
  datePickerText: {
    fontSize: 16,
  },
  bookButton: {
    backgroundColor: "#4CAF50",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
  },
  bookButtonText: {
    fontSize: 18,
    color: "#fff",
    fontWeight: "bold",
  },
  errorText: {
    fontSize: 18,
    color: "red",
    textAlign: "center",
    marginTop: 20,
  },
  modalContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
  },
  modalContent: {
    width: 300,
    padding: 20,
    backgroundColor: "#fff",
    borderRadius: 10,
    alignItems: "center",
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 20,
  },
  modalButton: {
    backgroundColor: "#4CAF50",
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
    width: "100%",
    alignItems: "center",
  },
  modalButtonText: {
    color: "#fff",
    fontSize: 16,
  },
  modalCloseButton: {
    backgroundColor: "#f44336",
    padding: 10,
    borderRadius: 5,
    width: "100%",
    alignItems: "center",
  },
  modalCloseButtonText: {
    color: "#fff",
    fontSize: 16,
  },
});

export default OfficeDetails;