import React from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';

const ImageCarousel = ({ images }) => {
    const carouselStyle = {
        width: '70%',
        margin: '0 auto'
    };

    return (
        <div style={carouselStyle}>
        <Carousel>
            {images.map((img) => (
                <div key={img.id}>
                    <img src={`data:image/png;base64,${img.data}`} alt={`Image ID: ${img.id}`}  />
                </div>
            ))}
        </Carousel>
        </div>
    );
};

export default ImageCarousel;
