import React from "react";
import { View, StyleSheet, ScrollView } from "react-native";
import ListingItem from "./listing_item";

interface Amenity {
  id: number;
  name: string;
}

interface Image {
  id: number;
  data: string | null;
}

interface Listing {
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
  amenities: Amenity[];
  images: Image[];
}

interface ListingListProps {
  listings: Listing[];
  onViewDetails: (officeInfo: Listing) => void;
}

const ListingList: React.FC<ListingListProps> = ({ listings, onViewDetails }) => {
  return (
    <ScrollView contentContainerStyle={styles.container}>
      {listings.map((listing) => (
        <ListingItem
          key={listing.id}
          id={listing.id}
          images={listing.images}
          name={listing.name}
          address={listing.address}
          city={listing.city}
          country={listing.country}
          postalCode={listing.postalCode}
          floor={listing.floor}
          roomNumber={listing.roomNumber}
          metricArea={listing.metricArea}
          price={listing.price}
          amenities={listing.amenities}
          onViewDetails={() => onViewDetails(listing)}
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