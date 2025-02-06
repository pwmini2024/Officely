import React, { createContext, useState, useContext } from 'react';

type PhotoContextType = {
  photoUri: string;
  setPhotoUri: (uri: string) => void;
};

const defaultPhotoUri = 'https://reactjs.org/logo-og.png';
const PhotoContext = createContext<PhotoContextType>({
  photoUri: defaultPhotoUri,
  setPhotoUri: () => {},
});

export const PhotoProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [photoUri, setPhotoUri] = useState<string>(defaultPhotoUri);

  return (
    <PhotoContext.Provider value={{ photoUri, setPhotoUri }}>
      {children}
    </PhotoContext.Provider>
  );
};

export const usePhotoContext = () => useContext(PhotoContext);
