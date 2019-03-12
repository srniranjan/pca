package com.askspock.pca;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class MnistDataFile implements Iterable< MnistDataFile.Tuple< BufferedImage, String > > {
    public static final int MNIST_MAGIC_NUMBER_IMAGE = 2051;
    public static final int MNIST_MAGIC_NUMBER_LABEl = 2049;


    private int _numberOfImages = 0;
    private int _numberOfRows = 0;
    private int _numberOfColumns = 0;
    private int _numberOfPixels = 0;
    private int _idx = 0;

    private String _labelFilePath = null;
    private String _imageFilePath = null;
    private FileInputStream _inImage = null;
    private FileInputStream _inLabel = null;

    private Tuple< BufferedImage, String > currentRecord = null;


    /**
     * Initialises an instance based on the input parameters
     *
     * @param labelFilePath path to mnist label file
     * @param imageFilePath path to mnist image file
     */
    public MnistDataFile( String labelFilePath, String imageFilePath ) {
        _labelFilePath = labelFilePath;
        _imageFilePath = imageFilePath;

        init();
    }


    private void init() {
        try {
            _inLabel = new FileInputStream( _labelFilePath );
            _inImage = new FileInputStream( _imageFilePath );


            // The very first 32 bytes of the input files contain the magic number. This magic number specifies the content
            // of the file and should be used for validation. Since init() is called after the input files has been
            // positively validated (using isValidMnistFile method), it is not needed anymore, and we have to skip it.
            _inImage.read();
            _inImage.read();
            _inImage.read();
            _inImage.read();

            // doing just the same for the label file
            _inLabel.read();
            _inLabel.read();
            _inLabel.read();
            _inLabel.read();


            // The input files are presented as "Big endian" format, so it has to be read in this way! i.e. transforming
            // it from "Big endian" into "Little endian" while reading the file in. It is done by reading a single byte
            // from the input, and then shifting it to the right.
            _numberOfImages = ( _inImage.read() << 24 ) | ( _inImage.read() << 16 ) | ( _inImage.read() << 8 ) | ( _inImage.read() );
            _numberOfRows = ( _inImage.read() << 24 ) | ( _inImage.read() << 16 ) | ( _inImage.read() << 8 ) | ( _inImage.read() );
            _numberOfColumns = ( _inImage.read() << 24 ) | ( _inImage.read() << 16 ) | ( _inImage.read() << 8 ) | ( _inImage.read() );

            _numberOfPixels = _numberOfRows * _numberOfColumns;

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }


    /**
     * returns the next record from the underlying mnist files in a form of a tuple, where the first element corresponds
     * to the image, and the second element corresponds to its label.
     *
     * @return
     * @throws EOFException
     */
    public Tuple< BufferedImage, String > getNextRecord() throws EOFException {

        // we have reached the end of the input file, no image should be returned.
        if( _idx >= _numberOfImages )
            throw new EOFException( "end of file has reached. No further input data is available" );

        BufferedImage currentImage = null;
        String currentLabel = null;


        try {
            currentImage = new BufferedImage( _numberOfColumns, _numberOfRows, BufferedImage.TYPE_INT_ARGB );
            int[] imgPixels = new int[ _numberOfPixels ];

            for( int p = 0; p < _numberOfPixels; p++ ) {
                int gray = 255 - _inImage.read();
                imgPixels[ p ] = 0xFF000000 | ( gray << 16 ) | ( gray << 8 ) | gray;
            }

            currentImage.setRGB( 0, 0, _numberOfColumns, _numberOfRows, imgPixels, 0, _numberOfColumns );
            Integer labelInt = _inLabel.read();
            currentLabel = labelInt.toString();

            _idx++;

        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        return new Tuple<>( currentImage, currentLabel );
    }


    /**
     * returns the dimension of the image as a {@link Tuple}. The first element corresponds to the
     * width of the image, and the second elements corresponds to its height.
     *
     * @return image dimension
     */
    public Tuple< Integer, Integer > getImageSize() {
        int w = 0;
        int h = 0;

        if( currentRecord != null ) {
            w = currentRecord.getFirst().getWidth();
            h = currentRecord.getFirst().getHeight();
        }

        return new Tuple<>( w, h );
    }


    /**
     * Returns the current record as {@link Tuple}.
     *
     * @return
     */
    public Tuple< BufferedImage, String > getCurrentRecord() {
        return this.currentRecord;
    }


    /**
     * Retrieves the total number of images/labels present in the underlying mnist files.
     *
     * @return number of entries in the underlying mnist files
     */
    public int bufferSize() {
        return _numberOfImages;
    }


    /**
     * Validates the given mnist image/label files and determines whether they are valid.
     *
     * @param fileName    path to mnist image or label file
     * @param isImageFile flag to signal whether the the validation should look for mnist image file or label file
     * @return validation result
     */
    public static boolean isValidMnistFile( String fileName, boolean isImageFile ) {
        boolean result;

        try {
            if( isImageFile ) {
                result = getIdxFileMagicNumber( fileName ) == MNIST_MAGIC_NUMBER_IMAGE;

            } else {
                result = getIdxFileMagicNumber( fileName ) == MNIST_MAGIC_NUMBER_LABEl;
            }
        }
        catch( IOException e ) {
            result = false;
        }

        return result;
    }


    private static int getIdxFileMagicNumber( String fileName ) throws IOException {
        int magicNumber;
        FileInputStream inputFile;

        inputFile = new FileInputStream( fileName );
        magicNumber = ( inputFile.read() << 24 ) | ( inputFile.read() << 16 ) | ( inputFile.read() << 8 ) | ( inputFile.read() );
        inputFile.close();

        return magicNumber;
    }


    @Override
    public Iterator< Tuple< BufferedImage, String > > iterator() {
        Iterator< Tuple< BufferedImage, String > > it = new Iterator< Tuple< BufferedImage, String > >() {

            @Override
            public boolean hasNext() {
                return _idx < _numberOfImages;
            }

            @Override
            public Tuple< BufferedImage, String > next() {
                try {
                    currentRecord = getNextRecord();
                    return currentRecord;
                }
                catch( EOFException e ) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        return it;
    }


    /**
     * Simple data structure for a pair of values.
     */
    public static class Tuple< E, T > {

        private E first;
        private T second;

        public Tuple( E first, T second ) {
            this.first = first;
            this.second = second;
        }

        public E getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

    }

}
