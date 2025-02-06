import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Text, TextInput, TouchableOpacity, Alert, Platform } from 'react-native';
import DateTimePickerModal from 'react-native-modal-datetime-picker';
import * as ImagePicker from 'expo-image-picker';
import { usePhotoContext } from './photo_context';
import { SafeAreaView } from 'react-native-safe-area-context';

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;

export default function RegisterScreen({ navigation }) {
  const { photoUri, setPhotoUri } = usePhotoContext();
  const [name, setName] = useState('');
  const [surname, setSurname] = useState('');
  const [email, setEmail] = useState('');
  const [dob, setDob] = useState(new Date());
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
  const [phone, setPhone] = useState('');
  const [isFormValid, setIsFormValid] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleAddImage = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== 'granted') {
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      quality: 1,
    });

    if (!result.canceled) {
      setPhotoUri(result.assets[0].uri);
    }
  };

  useEffect(() => {
    const validateForm = () => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      const isEmailValid = emailRegex.test(email);
      const isNameValid = name.trim().length > 0;
      const isSurnameValid = surname.trim().length > 0;
      const isPhoneValid = phone.trim().length > 0;

      setIsFormValid(isEmailValid && isNameValid && isSurnameValid && isPhoneValid);
    };

    validateForm();
  }, [email, name, surname, phone]);

  const handleRegister = async () => {
    setIsLoading(true);
    const formattedDob = `${dob.getFullYear()}-${String(dob.getMonth() + 1).padStart(2, '0')}-${String(dob.getDate()).padStart(2, '0')}`;
    const requestBody = {
      user: {
        email,
        name,
        surname,
        birthDate: formattedDob,
        phoneNumber: phone,
      },
    };
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/users`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      });

      if (response.ok) {
        const data = await response.json();
        Alert.alert('Success', 'User registered successfully');
        navigation.navigate('SignIn');
      } else {
        const errorData = await response.json();
        console.error('Error Response:', errorData);
        console.error('Status Code:', response.status);
        if (response.status === 409) {
          Alert.alert('Error', 'Email already exists');
        } else {
          Alert.alert('Error', errorData.message || 'Failed to register user');
        }
      }
    } catch (error) {
      console.error('Catch Error:', error);
      Alert.alert('Error', 'Failed to register user');
    } finally {
      setIsLoading(false);
    }
  };

  const showDatePicker = () => {
    setDatePickerVisibility(true);
  };

  const hideDatePicker = () => {
    setDatePickerVisibility(false);
  };

  const handleConfirm = (date) => {
    setDob(date);
    hideDatePicker();
  };

  return (
    <SafeAreaView style={styles.container}>
    <View style={styles.container}>
      <Text style={styles.header}>Register</Text>
      <TextInput
        style={styles.input}
        placeholder="Name"
        value={name}
        onChangeText={setName}
      />
      <TextInput
        style={styles.input}
        placeholder="Surname"
        value={surname}
        onChangeText={setSurname}
      />
      <TextInput
        style={styles.input}
        placeholder="Email"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />
      <TouchableOpacity onPress={showDatePicker} style={styles.input}>
        <Text>{dob.toDateString()}</Text>
      </TouchableOpacity>
      <DateTimePickerModal
        isVisible={isDatePickerVisible}
        mode="date"
        onConfirm={handleConfirm}
        onCancel={hideDatePicker}
      />
      <TextInput
        style={styles.input}
        placeholder="Phone number"
        keyboardType="phone-pad"
        value={phone}
        onChangeText={setPhone}
      />
      <TouchableOpacity
        style={[styles.registerButton, (!isFormValid || isLoading) && styles.disabledButton]}
        disabled={!isFormValid || isLoading}
        onPress={handleRegister}
      >
        <Text style={styles.registerButtonText}>{isLoading ? 'Registering...' : 'Register'}</Text>
      </TouchableOpacity>
      <Text style={styles.footerText}>Already have an account?</Text>
      <TouchableOpacity style={styles.signInButton} onPress={() => navigation.navigate('SignIn')}>
        <Text style={styles.signInText}>Sign in</Text>
      </TouchableOpacity>
    </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
   margin: 20,
  },
  header: {
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  input: {
    width: '100%',
    height: 50,
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    paddingHorizontal: 15,
    marginBottom: 15,
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: '#E0E0E0',
  },
  registerButton: {
    backgroundColor: '#000000',
    width: '100%',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 20,
  },
  disabledButton: {
    backgroundColor: '#A0A0A0',
  },
  registerButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
  },
  footerText: {
    fontSize: 14,
    color: '#666666',
    marginBottom: 10,
  },
  signInButton: {
    backgroundColor: '#E0E0E0',
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
    width: '100%',
  },
  signInText: {
    color: '#000000',
    fontSize: 16,
  },
});