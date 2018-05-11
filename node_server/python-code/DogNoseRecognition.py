import cv2
import glob
import time
import sys
import Preprocessing
import Matching
from image_enhance import image_enhance
from operator import eq


# Pre Processing methods
def pre_processing_method(img):

    img_gray = Preprocessing.gray_scaling(img)
    img_mask = Preprocessing.cropping3(img_gray)
    img_result = Preprocessing.equalization(img_mask)
    return img_result


def pre_processing_method_test(img):

    img_gray = Preprocessing.gray_scaling(img)
    img_mask = Preprocessing.cropping2(img_gray)
    img_eq = Preprocessing.equalization(img_mask)
    img_filter = image_enhance(img_eq)
    return img_filter


def pre_processing_method_test2(img):

    img_fin = Preprocessing.fining(img)
    img_mask = Preprocessing.cropping3(img_fin)
    img_eq = Preprocessing.equalization(img_mask)
    img_thin = Preprocessing.thinning(img_eq)

    return img_thin


def pre_processing_method_test3(img):

    img_fin = Preprocessing.fining(img)
    img_mask = Preprocessing.cropping3(img_fin)
    img_eq = Preprocessing.equalization(img_mask)
    img_filter = image_enhance(img_eq)

    return img_filter


# test4 differs for direction
def pre_processing_method_test4(img):

    img_gray = Preprocessing.gray_scaling(img)
    img_mask = Preprocessing.cropping3(img_gray)
    img_eq = Preprocessing.equalization(img_mask)
    img_filter = image_enhance(img_eq)

    return img_filter


def comparing(img_dog1, img_dog2):
    match_dog1 = pre_processing_method_test3(Preprocessing.resize(img_dog1, 400, 400))
    match_dog2 = pre_processing_method_test3(Preprocessing.resize(img_dog2, 400, 400))
    score2 = Matching.SURFMatching(match_dog1, match_dog2)
    print("score : ", score2)


# calculate the matching result of two image
def comparing_folder(img_dog1, img_dog2):
    score_total = 0
    for i in img_dog1:
        for j in img_dog2:
            if eq(i, j):
                continue
            else:
                match3 = pre_processing_method_test3(Preprocessing.resize(cv2.imread(i), 200, 200))
                match4 = pre_processing_method_test3(Preprocessing.resize(cv2.imread(j), 200, 200))
                score1 = Matching.SIFTMatching(match3, match4)
                #score1 = Matching.ORBMatching(match3, match4)
                #score1 = Matching.SURFMatching(match3, match4)

                print(i, " and ", j, " = ", score1)
                score_total = score_total + score1
    score_average = score_total / (len(img_dog1) * len(img_dog2[:-1]))
    return score_average


def comparing_result(image_input, path_of_input, image_database):
    score_total = 0
    img_num = 0
    for i in image_database:
        if eq(path_of_input, i):
            continue
        else:
            match_input = pre_processing_method(Preprocessing.resize(image_input, 400, 400))
            match_db = pre_processing_method(Preprocessing.resize(cv2.imread(i), 400, 400))
            score_result = Matching.SIFTMatching(match_input, match_db)
            print(i, " = ", score_result)
            score_total = score_total + score_result
            img_num = img_num + 1
    if img_num == 0:
        print('no image comes in')
        return -1
    else:
        score_average = score_total / (img_num)
        return score_average


if __name__ == "__main__":
    # OpenCV version
    print("OpenCV Version : " + cv2.__version__ + "\n")

    # 1) For Pre-Process Testing
    '''
    img1 = cv2.imread('images/etc/f_example1.png')
    img_test = pre_processing_method_test(Preprocessing.resize(img1, 400, 400))
    cv2.imshow("Pre-Processing", img_test)
    '''

    # 2) Matching Result for two image
    '''
    img_db_dog = cv2.imread('images/dog6/4.PNG')
    img_dog_1 = cv2.imread('images/dog5/1.PNG')
    img_dog_2 = cv2.imread('images/dog5/2.PNG')
    img_dog_3 = cv2.imread('images/dog5/3.PNG')
    start_time = time.time()
    comparing(img_db_dog, img_dog_1)
    comparing(img_db_dog, img_dog_2)
    comparing(img_db_dog, img_dog_3)
    print("--- %s seconds ---" % (time.time() - start_time))
    '''

    # 3) Matching Result for two folders (dog1~dog5)
    '''
    img_dog_a = glob.glob('images/dog2/*.png')
    img_dog_b = glob.glob('images/dog2/*.png')
    img_dog_a.sort()
    img_dog_b.sort()
    start_time = time.time()
    average = comparing_folder(img_dog_a, img_dog_b)
    print("평균은 : ", average)
    print("--- %s seconds ---" % (time.time() - start_time))
    '''

    # 4) Recognition
    # Execution example)
    # python DogNoseRecognition inputImage.png dog3

    if len(sys.argv) == 1:
        path_input = 'images/dog6/1.png'
        path_data = 'images/dog3/*.png'

    elif len(sys.argv) == 3:
        path_input = 'images/' + sys.argv[1]
        path_data = 'images/' + sys.argv[2] + '/*.png'

    print(path_input)
    print(path_data)

    img_input = cv2.imread(path_input)
    img_database = glob.glob(path_data)
    img_database.sort()
    start_time = time.time()
    average_result = comparing_result(img_input, path_input, img_database)
    print("평균은 : ", round(average_result, 3))
    print("--- %s seconds ---" % round((time.time() - start_time), 5))

    if average_result > 50:
        print('It is Same Dog')
    elif average_result == -1:
        print('Some Error in input Image')
    else:
        print('It is Different Dog')
    cv2.waitKey(0)  # Waits forever for user to press any key
    cv2.destroyAllWindows()  # Closes displayed windows
