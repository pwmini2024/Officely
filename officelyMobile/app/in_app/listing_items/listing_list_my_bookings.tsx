import React from "react";
import { View, StyleSheet, ScrollView } from "react-native";
import ListingItem from "./listing_item_my_bookings";

interface OfficeDto {
  id: string;
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
  images: { id: number; data: string | null }[];
}

interface Booking {
  id: string;
  bookedAt: string;
  comments: string;
  duration: number;
  endTime: string;
  officeDto: OfficeDto;
  paid: boolean;
  paidAt: string | null;
  paymentType: string;
  priceMultiplier: number;
  pricePerDay: number;
  startTime: string;
  status: string;
  totalPrice: number;
}

interface ListingListProps {
  bookings: Booking[];
}

const ListingList: React.FC<ListingListProps> = ({ bookings }) => {
  const sortedBookings = bookings.sort((a, b) => {
    if (a.status === "CANCELLED" && b.status !== "CANCELLED") {
      return 1;
    }
    if (a.status !== "CANCELLED" && b.status === "CANCELLED") {
      return -1;
    }
    return new Date(a.startTime).getTime() - new Date(b.startTime).getTime();
  });

  return (
    <ScrollView contentContainerStyle={styles.container}>
      {sortedBookings.map((booking, index) => (
        <ListingItem
          key={index}
          bookingId={booking.id}
          id={booking.officeDto.id}
          images={booking.officeDto.images}
          name={booking.officeDto.name}
          address={booking.officeDto.address}
          city={booking.officeDto.city}
          country={booking.officeDto.country}
          postalCode={booking.officeDto.postalCode}
          floor={booking.officeDto.floor}
          roomNumber={booking.officeDto.roomNumber}
          metricArea={booking.officeDto.metricArea}
          price={booking.totalPrice}
          amenities={booking.officeDto.amenities}
          startTime={booking.startTime}
          endTime={booking.endTime}
          status={booking.status}
        />
      ))}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
  },
});

export default ListingList;
