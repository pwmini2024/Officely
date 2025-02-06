import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Image,
  Dimensions,
  Alert,
  Modal,
  Platform,
} from "react-native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { useAuth } from "../context/AuthContext";
import { SafeAreaView } from "react-native-safe-area-context";


const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

const { width: viewportWidth } = Dimensions.get("window");

const formatDateToLocalISO = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const getToday = () => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return today;
};

const OfficeDetailsMyBookings = ({ route, navigation }) => {
  const { officeInfo } = route.params;
  const { email } = useAuth();
  const [images, setImages] = useState([]);
  const [imageSource, setImageSource] = useState<string | null>(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [startDate, setStartDate] = useState(new Date(officeInfo.startTime));
  const [endDate, setEndDate] = useState(new Date(officeInfo.endTime));
  const [tempStartDate, setTempStartDate] = useState(new Date(officeInfo.startTime));
  const [tempEndDate, setTempEndDate] = useState(new Date(officeInfo.endTime));
  const [isStartDatePickerVisible, setStartDatePickerVisibility] = useState(false);
  const [isEndDatePickerVisible, setEndDatePickerVisibility] = useState(false);

  useEffect(() => {
    if (officeInfo && officeInfo.images && officeInfo.images.length > 0) {
      const fetchImages = async () => {
        const imagePromises = officeInfo.images.map((image) =>
          fetchImageById(image.id)
        );
        const imageData = await Promise.all(imagePromises);
        setImages(
          imageData
            .filter((data) => data && data.data)
            .map((data) => `data:image/jpeg;base64,${data.data}`)
        );
      };
      fetchImages();
    }
  }, [officeInfo]);

  const fetchImageById = async (id) => {
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/images/${id}`, {
        method: "GET",
        headers: {
          Authorization: email,
          "Content-Type": "application/json",
        },
      });
      if (response.ok) {
        const data = await response.json();
        return data;
      } else {
        console.error("Error fetching image:", response.status);
        return null;
      }
    } catch (error) {
      console.error("Error fetching image:", error);
      return null;
    }
  };

  useEffect(() => {
    if (images && images.length > 0) {
      setImageSource(images[0]);
    }
  }, [images]);

  const calculateDays = (start, end) => {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const timeDiff = endDate.getTime() - startDate.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
    return daysDiff;
  };

  const handleCancelBooking = () => {
    Alert.alert(
      "Cancel Booking",
      "Are you sure you want to cancel this booking?",
      [
        {
          text: "No",
          style: "cancel",
        },
        {
          text: "Yes",
          onPress: async () => {
            try {
              const response = await fetch(
                `${AZURE_ENDPOINT}/reservations/${officeInfo.bookingId}`,
                {
                  method: "DELETE",
                  headers: {
                    "Content-Type": "application/json",
                    Authorization: email,
                  },
                  body: JSON.stringify({ email }),
                }
              );

              if (response.ok) {
                Alert.alert("Success", "Booking canceled successfully");
                navigation.goBack();
              } else {
                const errorData = await response.json();
                Alert.alert(
                  "Error",
                  errorData.message || "Failed to cancel booking"
                );
              }
            } catch (error) {
              Alert.alert("Error", "Failed to cancel booking");
            }
          },
        },
      ]
    );
  };

  const handleChangeDetails = () => {
    setModalVisible(true);
  };

  const handleStartConfirm = (date) => {
    const today = getToday();

    if (date < today) {
      Alert.alert("Invalid Date", "Start date cannot be earlier than today.");
      return;
    }

    setTempStartDate(date);

    if (date > tempEndDate) {
      setTempEndDate(date);
    }

    setStartDatePickerVisibility(false);
  };

  const handleEndConfirm = (date) => {
    if (date < tempStartDate) {
      Alert.alert("Invalid Date", "End date cannot be earlier than start date.");
      return;
    }

    setTempEndDate(date);
    setEndDatePickerVisibility(false);
  };

  const handleSaveChanges = async () => {
    setModalVisible(false);

    try {
      const response = await fetch(
        `${AZURE_ENDPOINT}/reservations/${officeInfo.bookingId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: email,
          },
          body: JSON.stringify({
            email,
            reservation: {
              startTime: formatDateToLocalISO(tempStartDate),
              endTime: formatDateToLocalISO(tempEndDate),
              paymentType: "CARD",
              comments: "Reservation updated",
            },
          }),
        }
      );

      if (response.ok) {
        setStartDate(tempStartDate);
        setEndDate(tempEndDate);
        Alert.alert("Success", "Booking details updated successfully");
      } else {
        const errorData = await response.json();
        Alert.alert("Error", errorData.message || "Failed to update booking");
      }
    } catch (error) {
      Alert.alert("Error", "Failed to update booking");
    }
  };

  if (!officeInfo) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>No office information available.</Text>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.backArrow}>{"<"}</Text>
        </TouchableOpacity>
        <Text style={styles.headerText}>User App - Office Details</Text>
      </View>
      <View style={styles.imageContainer}>
        {imageSource ? (
          <Image source={{ uri: imageSource }} style={styles.image} />
        ) : (
          <Image source={{ uri: "placeholder.png" }} style={styles.image} />
        )}
      </View>
      <View style={styles.content}>
        <Text style={styles.officeTitle}>{officeInfo.name}</Text>
        <Text style={styles.location}>
          Location: {officeInfo.address}, {officeInfo.city},{" "}
          {officeInfo.country}, {officeInfo.postalCode}
        </Text>
        <Text style={styles.location}>Floor: {officeInfo.floor}</Text>
        <Text style={styles.location}>
          Room Number: {officeInfo.roomNumber}
        </Text>
        <Text style={styles.location}>Area: {officeInfo.metricArea} m²</Text>
        <Text style={styles.location}>
          Start Date: {startDate.toDateString()}
        </Text>
        <Text style={styles.location}>End Date: {endDate.toDateString()}</Text>
        <Text style={styles.location}>Status: {officeInfo.status}</Text>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Amenities</Text>
          <Text style={styles.sectionContent}>
            {officeInfo.amenities && officeInfo.amenities.length > 0 ? (
              officeInfo.amenities.map((amenity, index) => (
                <Text key={index}>
                  • {amenity.name}
                  {"\n"}
                </Text>
              ))
            ) : (
              <Text>No amenities available.</Text>
            )}
          </Text>
        </View>

        <Text style={styles.price}>
          {officeInfo.price} PLN / {calculateDays(startDate, endDate)} Day(s)
        </Text>

        {officeInfo.status !== "CANCELLED" && (
          <View style={styles.buttonContainer}>
            <TouchableOpacity
              style={styles.button}
              onPress={handleCancelBooking}
            >
              <Text style={styles.buttonText}>Cancel Booking</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.button}
              onPress={handleChangeDetails}
            >
              <Text style={styles.buttonText}>Change Details</Text>
            </TouchableOpacity>
          </View>
        )}
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
            <Text style={styles.modalTitle}>Change Booking Details</Text>
            <View style={styles.datePickerContainer}>
              <TouchableOpacity
                onPress={() => setStartDatePickerVisibility(true)}
                style={styles.datePickerButton}
              >
                <Text style={styles.datePickerText}>
                  Start Date: {tempStartDate.toDateString()}
                </Text>
              </TouchableOpacity>
              <DateTimePickerModal
                isVisible={isStartDatePickerVisible}
                mode="date"
                onConfirm={handleStartConfirm}
                onCancel={() => setStartDatePickerVisibility(false)}
                minimumDate={getToday()}
              />
              <TouchableOpacity
                onPress={() => setEndDatePickerVisibility(true)}
                style={styles.datePickerButton}
              >
                <Text style={styles.datePickerText}>
                  End Date: {tempEndDate.toDateString()}
                </Text>
              </TouchableOpacity>
              <DateTimePickerModal
                isVisible={isEndDatePickerVisible}
                mode="date"
                onConfirm={handleEndConfirm}
                onCancel={() => setEndDatePickerVisibility(false)}
                minimumDate={tempStartDate}
              />
            </View>
            <TouchableOpacity
              style={styles.saveButton}
              onPress={handleSaveChanges}
            >
              <Text style={styles.saveButtonText}>Save Changes</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={styles.closeButton}
              onPress={() => {
                setModalVisible(false);
                setTempStartDate(startDate);
                setTempEndDate(endDate);
              }}
            >
              <Text style={styles.closeButtonText}>Close</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </ScrollView>
    </SafeAreaView>
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
  imageContainer: {
    marginBottom: 10,
  },
  image: {
    width: viewportWidth,
    height: 200,
    borderRadius: 8,
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
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 20,
  },
  button: {
    backgroundColor: "#5B5F97",
    padding: 10,
    borderRadius: 5,
    width: "48%",
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontSize: 14,
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
  saveButton: {
    backgroundColor: "#4CAF50",
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
    width: "100%",
    alignItems: "center",
  },
  saveButtonText: {
    color: "#fff",
    fontSize: 16,
  },
  closeButton: {
    backgroundColor: "#f44336",
    padding: 10,
    borderRadius: 5,
    width: "100%",
    alignItems: "center",
  },
  closeButtonText: {
    color: "#fff",
    fontSize: 16,
  },
  errorText: {
    fontSize: 18,
    color: "red",
    textAlign: "center",
    marginTop: 20,
  },
});

export default OfficeDetailsMyBookings;