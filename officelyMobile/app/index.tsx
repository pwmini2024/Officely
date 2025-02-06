import React, { useEffect } from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";
import {
  NavigationContainer,
  NavigationIndependentTree,
} from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import { createDrawerNavigator } from "@react-navigation/drawer";
import RegisterScreen from "./register";
import SignInScreen from "./sign_in";
import Home from "./in_app/home";
import { PhotoProvider } from "./photo_context";
import { AuthProvider, useAuth } from "./context/AuthContext";
import OfficeDetails from "./in_app/office_details";
import MyProfile from "./in_app/my_profile";
import MyBookings from "./in_app/my_bookings";
import OfficeDetails_MyBooking from "./in_app/office_details_my_bookings";
import ParkingList from './in_app/parking_list';

const Stack = createStackNavigator();
const Drawer = createDrawerNavigator();

function WelcomeScreen({ navigation }) {
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Welcome to Officely!</Text>
      <Text style={styles.subheader}>Explore best offices to rent.</Text>
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={styles.registerButton}
          onPress={() => navigation.navigate("Register")}
        >
          <Text style={styles.registerText}>Register</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.signInButton}
          onPress={() => navigation.navigate("SignIn")}
        >
          <Text style={styles.signInText}>Sign in</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

function LogoutScreen({ navigation }) {
  const { logout } = useAuth();

  useEffect(() => {
    const performLogout = async () => {
      await logout();
      navigation.reset({
        index: 0,
        routes: [{ name: 'Welcome' }],
      });
    };
    performLogout();
  }, [logout, navigation]);

  return null;
}

function DrawerNavigator() {
  return (
    <Drawer.Navigator>
      <Drawer.Screen name="Home" component={Home} />
      <Drawer.Screen name="My Bookings" component={MyBookings} />
      <Drawer.Screen name="My Profile" component={MyProfile} />
      <Drawer.Screen name="Logout" component={LogoutScreen} />
    </Drawer.Navigator>
  );
}

function MainStackNavigator() {
  const { email } = useAuth();

  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      {email ? (
        <>
          <Stack.Screen name="Drawer" component={DrawerNavigator} />
          <Stack.Screen name="OfficeDetails" component={OfficeDetails} />
          <Stack.Screen name="OfficeDetailsMyBookings" component={OfficeDetails_MyBooking} />
          <Stack.Screen name="ParkingList" component={ParkingList} />
        </>
      ) : (
        <>
          <Stack.Screen name="Welcome" component={WelcomeScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
          <Stack.Screen name="SignIn" component={SignInScreen} />
        </>
      )}
    </Stack.Navigator>
  );
}

export default function App() {
  return (
    <NavigationIndependentTree>
      <AuthProvider>
        <PhotoProvider>
          <NavigationContainer>
            <MainStackNavigator />
          </NavigationContainer>
        </PhotoProvider>
      </AuthProvider>
    </NavigationIndependentTree>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 50,
    fontWeight: "bold",
    marginBottom: 10,
  },
  subheader: {
    fontSize: 16,
    color: "#666666",
    textAlign: "center",
    marginBottom: 40,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    width: "100%",
    paddingHorizontal: 40,
  },
  registerButton: {
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#000000",
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  registerText: {
    color: "#000000",
    fontSize: 16,
  },
  signInButton: {
    backgroundColor: "#000000",
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  signInText: {
    color: "#FFFFFF",
    fontSize: 16,
  },
});
