/*------------------------------------------------------*/
/* Prog    : Tp2_IFT3205-2-6.c                          */
/* Auteur  :  Abderrahim Tabta & Maxime Ton             */
/* Date    : --/--/2010                                 */
/* version :                                            */ 
/* langage : C                                          */
/* labo    : DIRO                                       */
/*------------------------------------------------------*/

/*------------------------------------------------*/
/* FICHIERS INCLUS -------------------------------*/
/*------------------------------------------------*/
#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#include "FonctionDemo2.h"

/*------------------------------------------------*/
/* DEFINITIONS -----------------------------------*/   
/*------------------------------------------------*/
#define NAME_VISUALISER "display "
#define NAME_IMG_IN1  "UdM_1"
#define NAME_IMG_IN2  "UdM_2"
#define NAME_IMG_IN3 "image-TpIFT3205-2-4"
#define NAME_IMG_OUT1 "image-TpIFT3205-2-6"

/*------------------------------------------------*/
/* PROTOTYPE DE FONCTIONS  -----------------------*/   
/*------------------------------------------------*/

void VisualizeImg(float**, int, int);               // source : TP1  

// function that multiply each value by (-1)^i+j
void MyProcessImg(float** matrix, int length, int width){
  for(int i=0; i<length; i++) {
    for(int j=0; j<width; j++) {
      matrix[i][j]*= pow(-1, i+j);
    }
  }
}

void VisualizeImg(float** matrix,int length, int width) {
for(int i=0; i<length; i++) {
    for(int j=0; j<width; j++) {
      matrix[i][j] = log(1+matrix[i][j]);
    }
  }
}

void Rotate(float** matriceOut, float** matriceIn, float theta, int length, int width) { 
    // Calculate sine and cosine of the rotation angle
    const float cosTheta = cos(theta);
    const float sinTheta = sin(theta);

    // Center of the matrix to be rotated
    int centerY = width / 2 - ((width/2) * cosTheta - (length/2) * sinTheta);
    int centerX = length / 2 - ((width/2) * sinTheta +( length/2) * cosTheta);

    // Calculate the maximum i and j indices for the matrix
    int maxI = length - 1;
    int maxJ = width - 1;

    // Iterate over each pixel in the matrix
    for (int i = 0; i < length; i++) {
        for (int j = 0; j < width; j++) {

            // Apply the rotation transformation to the pixel coordinates (Counter-clockwise)
            float x = i ; 
            float y = j ; 
            float xPrime = x * cosTheta + y * sinTheta + centerX;       
            float yPrime = -x * sinTheta + y * cosTheta + centerY;
        
            // If the transformed coordinates are outside the bounds of the matrix, set the pixel value to 0
            if (xPrime < 0 || xPrime >= maxI || yPrime < 0 || yPrime >= maxJ) {
                matriceOut[i][j] = 0.0;
                
            // If the transformed coordinates correspond exactly to a pixel in the input matrix, set the pixel value to that of the input pixel
            } else if (ceilf(xPrime) == xPrime && ceilf(yPrime) == yPrime) {
                matriceOut[i][j] = matriceIn[(int)xPrime][(int)yPrime];

            // Otherwise, use bilinear interpolation to calculate the pixel value based on the values of the surrounding pixels
            } else {
                int xFloor = floorf(xPrime);
                int yFloor = floorf(yPrime);
                int xCeil = xFloor + 1;
                int yCeil = yFloor + 1;

                float f1 = matriceIn[xFloor][yFloor] + (xCeil - xPrime) * (matriceIn[xFloor][yCeil] - matriceIn[xFloor][yFloor]);
                float f2 = matriceIn[xCeil][yFloor] + (xPrime - xFloor) * (matriceIn[xCeil][yCeil] - matriceIn[xCeil][yFloor]);
                float f3 = f1 + (yCeil - yPrime) * (f2 - f1);
                matriceOut[i][j] = f3;
            }
        }
    }
}


float CalculateError(float** matriceM, float** matriceR, int length, int width){
  float error = 0.0;
  for(int i=0; i<length; i++){
    for(int j=0; j<width; j++){
      error += abs(abs(matriceR[i][j])-abs(matriceM[i][j]));
    }
  }
  return error;
}

float FindAngle(float** MatriceImgR2, float** MatriceImgM2, float** MatriceImgM1, float length, float width, float start, float end, float step, int debug){
  float errorMin = INFINITY; // Set the minimum error to infinity, so that the first error calculated will always be smaller
  float bestAngle = 0.0; // Set the best angle to zero
  float current; // Set the current resemblance error to zero
  
  for(float theta = start; theta<=end; theta+=step) { // For each possible value of theta (angle) between -pi/16 and pi/16, with a step of 0.005
    Rotate(MatriceImgR2, MatriceImgM2, theta, length, width); // Rotate the image by theta
    current = CalculateError(MatriceImgM1, MatriceImgR2, length, width); // Calculate the error between the two images

    if(debug){printf("[%f::%i]>",theta,(int)current);} // Print the current theta and the current resemblance error
    
    if(current < errorMin ){ // If the current resemblance error is smaller than the minimum error
      errorMin = current; // Set the minimum error to the current resemblance error
      bestAngle = theta; // Set the best angle to the current theta
    }
  }
  return bestAngle;
}

// Initialize a matrix by setting every element to 0.0.
void InitializeMatrix(float** matrix, int length, int width) {
  for (int i = 0; i < length; i++) {
    for (int j = 0; j < width; j++) {
      matrix[i][j] = 0.0;
    }
  }
}

// Initialize a matrix by setting every element to 0.0.
void InvertMatrix(float** matrix, int length, int width) {
  for (int i = 0; i < length; i++) {
    for (int j = 0; j < width; j++) {
      matrix[i][j] *= -1;
    }
  }
}

void DivideBySquares(float** matrixR, float** matrixI, float** div, int length, int width) {
  for (int i = 0; i < length; i++) {
    for (int j = 0; j < width; j++) {
      float divSquare = div[i][j] * div[i][j];
      matrixR[i][j] /= divSquare;
      matrixI[i][j] /= divSquare;
    }
  }
}

// this function will find the max value in the MatriceImgR3 and return the coordinates of the max value
void FindMaxVects(float** matrixR, int length, int width, int* vectX, int* vectY) {

    float maxValue = -INFINITY;
    int iMax = 0;
    int jMax = 0; 

    for (int i = 0; i < length; i++) {
        for (int j = 0; j < width; j++) {
            if (matrixR[i][j] > maxValue) {
                iMax = i;
                jMax = j;
                maxValue = matrixR[i][j];
            }
        }
    }

    *vectX = length / 2 - iMax;
    *vectY = width / 2 - jMax;
}

float** Merge(float** matrix1, float** matrix2, int length, int width, int tx, int ty){

  int total_length = length * 2; 
  int total_width = width * 2;  
  float** mergedImages = fmatrix_allocate_2d(total_length, total_width);


  int x_center = total_length / 2 - length / 2;
  int y_center = total_width/ 2 - width / 2;

  for (int i = 0; i < length; i++) {
    for (int j = 0; j < width; j++) {
      mergedImages[x_center + i][y_center + j] = matrix1[i][j];
      
    }
  }

  for (int i = 0; i < length; i++) {
    for (int j = 0; j < width; j++) {
      if (matrix2[i][j] >= 1) {
        mergedImages[x_center + i + tx][y_center + j + ty] = matrix2[i][j];
      }
    }
  }
  return mergedImages;
}

/*------------------------------------------------*/
/* PROGRAMME PRINCIPAL   -------------------------*/                     
/*------------------------------------------------*/
int main(int argc,char **argv)
 {
  int i,j,k;
  int length,width;
  float Theta0;
  int x0,y0;
  char BufSystVisuImg[100];

  //Constante
  length=512;
  width=512;
  

  //Allocation Memoire 
  float** MatriceImgI1=fmatrix_allocate_2d(length,width); // 0.0
  float** MatriceImgM1=fmatrix_allocate_2d(length,width);
  float** MatriceImgR1=fmatrix_allocate_2d(length,width);
  float** MatriceImgI2=fmatrix_allocate_2d(length,width); // 0.0
  float** MatriceImgM2=fmatrix_allocate_2d(length,width);
  float** MatriceImgR2=fmatrix_allocate_2d(length,width);
  float** MatriceImgI3=fmatrix_allocate_2d(length,width); // 0.0
  float** MatriceImgM3=fmatrix_allocate_2d(length,width);
  float** MatriceImgR3=fmatrix_allocate_2d(length,width);
  float** MatriceImgI4=fmatrix_allocate_2d(length,width);
  float** MatriceImgM4=fmatrix_allocate_2d(length,width);
  float** MatriceImgR4=fmatrix_allocate_2d(length,width);

  //float** MatriceImg3=fmatrix_allocate_2d(length,width);
  float** MatriceImgOut=fmatrix_allocate_2d(length,width);       

  // new 
  float** MatriceImgI1Copy=fmatrix_allocate_2d(length,width);
  float** MatriceImgM1Copy=fmatrix_allocate_2d(length,width);
  float** MatriceImgR1Copy=fmatrix_allocate_2d(length,width);

  float** MatriceImgI2Copy=fmatrix_allocate_2d(length,width);
  float** MatriceImgM2Copy=fmatrix_allocate_2d(length,width);
  float** MatriceImgR2Copy=fmatrix_allocate_2d(length,width);


  //Lecture Image 

  float** MatriceImg2=LoadImagePgm(NAME_IMG_IN1,&length,&width);  // UdM1
  float** MatriceImg3=LoadImagePgm(NAME_IMG_IN2,&length,&width);  // UdM2
  float** MatriceImg1=LoadImagePgm(NAME_IMG_IN3,&length,&width);  // Rotated
  float** MatriceImg1Copy=LoadImagePgm(NAME_IMG_IN1,&length,&width);  // UdM1 
  float** MatriceImg2Copy=LoadImagePgm(NAME_IMG_IN2,&length,&width);  // UdM2 

  // Initializes the three matrices to 0.0.       
  InitializeMatrix(MatriceImgI1, length, width);
  InitializeMatrix(MatriceImgI2, length, width);

  MyProcessImg(MatriceImg1Copy, length, width);    
  MyProcessImg(MatriceImg2Copy, length, width);

  FFTDD(MatriceImg1Copy, MatriceImgI1Copy, length, width);
  FFTDD(MatriceImg2Copy, MatriceImgI2Copy, length, width);

  Mod(MatriceImgM1Copy, MatriceImg1Copy, MatriceImgI1Copy, length, width);
  Mod(MatriceImgM2Copy, MatriceImg2Copy, MatriceImgI2Copy, length, width);

  float angle = FindAngle(MatriceImgR2Copy, MatriceImgM2Copy, MatriceImgM1Copy, length,width,-M_PI/16, M_PI/16, 0.005, 0);

  IFFTDD(MatriceImg1Copy, MatriceImgI1Copy, length, width);
  IFFTDD(MatriceImg2Copy, MatriceImgI2Copy, length, width);

  Rotate(MatriceImgR2Copy, MatriceImg3, angle, length, width);

  InitializeMatrix(MatriceImgI1, length, width);
  InitializeMatrix(MatriceImgI2, length, width);
  InitializeMatrix(MatriceImgI3, length, width);

  FFTDD(MatriceImg1, MatriceImgI1, length, width);
  FFTDD(MatriceImg2, MatriceImgI2, length, width);

  Mod(MatriceImgM1, MatriceImg1, MatriceImgI1, length, width);

  // Invert values of the image 
  InvertMatrix(MatriceImgI1, length, width);
  
  MultMatrix(MatriceImgR3, MatriceImgI3, MatriceImg1, MatriceImgI1, MatriceImg2, MatriceImgI2, length, width);

  DivideBySquares(MatriceImgR3, MatriceImgI3, MatriceImgM1, length, width);

  MyProcessImg(MatriceImgR3, length, width);
  MyProcessImg(MatriceImgI3, length, width);

  IFFTDD(MatriceImgR3, MatriceImgI3, length, width);

  Recal(MatriceImgR3, length, width);


  int vectX, vectY;
  FindMaxVects(MatriceImgR3, length, width, &vectX, &vectY);
  //printf("Le vecteur est: (%d, %d)\n", vectX, vectY);

  IFFTDD(MatriceImg2, MatriceImgI2, length, width);
  InvertMatrix(MatriceImgI1, length, width);
  IFFTDD(MatriceImg1, MatriceImgI1, length, width);

  float** pano = Merge(MatriceImgR2Copy, MatriceImg2, length, width, vectX, vectY);  

  //Sauvegarde
  SaveImagePgm(NAME_IMG_OUT1,pano,length*2,width*2);

  //Commande systeme: VISU
  strcpy(BufSystVisuImg,NAME_VISUALISER);
  strcat(BufSystVisuImg,NAME_IMG_OUT1);
  strcat(BufSystVisuImg,".pgm&");
  printf(" %s",BufSystVisuImg);
  system(BufSystVisuImg);

  //==End=========================================================

  //Liberation memoire 
  free_fmatrix_2d(MatriceImgR1);
  free_fmatrix_2d(MatriceImgI1);
  free_fmatrix_2d(MatriceImgM1);
  free_fmatrix_2d(MatriceImgR2);
  free_fmatrix_2d(MatriceImgI2);
  free_fmatrix_2d(MatriceImgM2);
  free_fmatrix_2d(MatriceImgR3);
  free_fmatrix_2d(MatriceImgI3);
  free_fmatrix_2d(MatriceImgM3);
  free_fmatrix_2d(MatriceImg1);
  free_fmatrix_2d(MatriceImg2);  
  free_fmatrix_2d(MatriceImg3);
  // TODO FREE EVERYTHING 

  //retour sans probleme
  printf("\n C'est fini ... \n\n");
  return 0; 	 
}