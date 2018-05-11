from ridge_segment import ridge_segment
from ridge_orient import ridge_orient
from ridge_freq import ridge_freq
from ridge_filter import ridge_filter
import cv2
import numpy as np


def image_enhance(img):
    blksze = 32   # default = 16
    thresh = 0.01 # default = 0.1

    # 32, 0.01 is good
    normim, mask = ridge_segment(img, blksze, thresh)  # normalise the image and find a ROI

    gradientsigma = 1
    blocksigma = 1
    orientsmoothsigma = 1
    orientim = ridge_orient(normim, gradientsigma, blocksigma, orientsmoothsigma)  # find orientation of every pixel

    # 15 5 5 15
    blksze = 31
    windsze = 5
    minWaveLength = 3
    maxWaveLength = 9
    # default 5 15

    # wavelength
    freq, medfreq = ridge_freq(normim, mask, orientim, blksze, windsze, minWaveLength, maxWaveLength)
    # find the overall frequency of ridges

    freq = medfreq * mask
    kx = 0.3
    ky = 0.3
    # 원래는 0.65
    newim = ridge_filter(normim, orientim, freq, kx, ky)  # create gabor filter and do the actual filtering

    # g_kernel
    #                            (ksize   , sigma,     , theta    ,lambda, gamma, psi, ktype)
    g_kernel = cv2.getGaborKernel((21, 21), np.pi/4 * 3, np.pi / 8, 1    , 10   , 0  , ktype=cv2.CV_32F)
    img_gabor = cv2.filter2D(img, cv2.CV_8UC3, g_kernel)

    th, bin_im = cv2.threshold(np.uint8(newim), 128, 255, cv2.THRESH_BINARY)

    return bin_im
