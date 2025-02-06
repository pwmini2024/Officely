import React, { useState, useEffect } from "react";
import {
  StyleSheet,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from "react-native";
import { useAuth } from './context/AuthContext';

const AZURE_ENDPOINT = process.env.EXPO_PUBLIC_AZURE;


export default function SignInScreen({ navigation }) {
  const [emailInput, setEmailInput] = useState("");
  const [isEmailValid, setIsEmailValid] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { setEmail } = useAuth();

  useEffect(() => {
    const validateEmail = () => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (emailRegex.test(emailInput)) {
        setIsEmailValid(true);
      } else {
        setIsEmailValid(false);
      }
    };

    validateEmail();
  }, [emailInput]);

  const handleSignIn = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(`${AZURE_ENDPOINT}/users`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": emailInput,
        },
        mode: "cors", 
        credentials: "include", 
      });

      if (response.ok) {
        const data = await response.json();
        setEmail(emailInput);
        Alert.alert("Success", "Signed in successfully");
      } else {
        const errorData = await response.json();
        console.error("Error Response:", errorData);
        console.error("Status Code:", response.status);
        Alert.alert("Error", errorData.message || "Failed to sign in");
      }
    } catch (error) {
      console.error("Catch Error:", error);
      Alert.alert("Error", "Failed to sign in");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Sign In</Text>
      <TextInput
        style={styles.input}
        placeholder="Email"
        keyboardType="email-address"
        value={emailInput}
        onChangeText={setEmailInput}
      />
      <TouchableOpacity
        style={[
          styles.button,
          (!isEmailValid || isLoading) && styles.disabledButton,
        ]}
        disabled={!isEmailValid || isLoading}
        onPress={handleSignIn}
      >
        {isLoading ? (
          <ActivityIndicator size="small" color="#FFFFFF" />
        ) : (
          <Text style={styles.buttonText}>Sign In</Text>
        )}
      </TouchableOpacity>
      <Text style={styles.text}>Don't have an account yet?</Text>
      <TouchableOpacity
        onPress={() => navigation.navigate("Register")}
        style={styles.button}
      >
        <Text style={styles.buttonText}>Register</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F5F5F5",
    alignItems: "center",
    justifyContent: "center",
    padding: 20,
  },
  header: {
    fontSize: 32,
    fontWeight: "bold",
    marginBottom: 20,
  },
  text: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
  },
  input: {
    width: "100%",
    height: 50,
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    paddingHorizontal: 15,
    marginBottom: 15,
    borderWidth: 1,
    borderColor: "#E0E0E0",
  },
  button: {
    backgroundColor: "#000000",
    width: "100%",
    padding: 15,
    borderRadius: 8,
    alignItems: "center",
    marginBottom: 20,
  },
  disabledButton: {
    backgroundColor: "#A0A0A0",
  },
  buttonText: {
    color: "#FFFFFF",
    fontSize: 16,
  },
});