import cv2
import numpy as np
# Pre-Processing Functions


def gray_scaling(img):
    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    return img_gray


def equalization(img):
    img_eq = cv2.equalizeHist(img)
    return img_eq


def fining(img):

    img2 = cv2.blur(img, (2, 2))
    hsv = cv2.cvtColor(img2, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv)
    #thresh2 = cv2.adaptiveThreshold(v, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY_INV, 11, 2)
    thresh2 = cv2.adaptiveThreshold(v, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 11, 2)
    return thresh2


def binarization(img):
    _, img_bi = cv2.threshold(img, 50   , 255, cv2.THRESH_BINARY)
    return img_bi


def binarization_s(img):
    #thre = calculate_mean_intensity(img)
    _, img_bi = cv2.threshold(img, 128, 255, cv2.THRESH_BINARY)
    return img_bi


def calculate_mean_intensity(img):

    col, row = img.shape[:2]
    sum = 0
    for i in range(0, 400):
        for j in range(0, 400):
            sum += img[i][j]
    mean = sum / 160000
    print(mean)
    return mean


def thinning(img):
    size = np.size(img)
    img_thin = np.zeros(img.shape, np.uint8)

    element = cv2.getStructuringElement(cv2.MORPH_CROSS, (3, 3))
    done = False

    while not done:
        eroded = cv2.erode(img, element)
        temp = cv2.dilate(eroded, element)
        temp = cv2.subtract(img, temp)
        img_thin = cv2.bitwise_or(img_thin, temp)
        img = eroded.copy()

        zeros = size - cv2.countNonZero(img)
        if zeros == size:
            done = True
    return img_thin


def gabor_filter(img):
    g_kernel = cv2.getGaborKernel((21, 21), 8.0, np.pi / 4, 10.0, 0.5, 0, ktype=cv2.CV_32F)
    img_gabor = cv2.filter2D(img, cv2.CV_8UC3, g_kernel)
    return img_gabor


def cropping(img):
    # cv2.circle(image, (X, Y), radius, (R, G, B), Thinckness)
    img_crop = img
    cv2.circle(img_crop, (350, 450), 500, (0, 0, 255), 500)
    return img_crop


def cropping2(img):
    # cv2.circle(image, (X, Y), radius, (R, G, B), Thickness)
    height, width = img.shape[:2]
    img_crop = cv2.copyMakeBorder(img, 0, 0, 0, 0, cv2.BORDER_CONSTANT, None)
    cv2.circle(img_crop, (int(height/2), int(width/2)), int(height), (0, 0, 255), int(width))
    return img_crop


def cropping3(img):
    height, width = img.shape[:2]
    img_crop = img[int(height/8):int(height*7/8), int(width/4):int(width*3/4)]
    return img_crop


# Resize the image
def resize(img, row, col):
    resize_img = cv2.resize(img, (row, col))
    return resize_img
