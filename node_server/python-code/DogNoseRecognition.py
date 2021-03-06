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
'''
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
'''

def comparing(img_dog1, img_dog2):
    match_dog1 = pre_processing_method_test3(Preprocessing.resize(img_dog1, 400, 400))
    match_dog2 = pre_processing_method_test3(Preprocessing.resize(img_dog2, 400, 400))
    score2 = Matching.SURFMatching(match_dog1, match_dog2)
    # print("score : ", score2)


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

                # print(i, " and ", j, " = ", score1)
                score_total = score_total + score1
    score_average = score_total / (len(img_dog1) * len(img_dog2[:-1]))
    return score_average


def comparing_result_verification(path_of_input, path_of_database):
    image_input = cv2.imread(path_of_input)
    image_database = glob.glob(path_of_database)
    image_database.sort()
    score_total = 0
    img_num = 0
    for i in image_database:
        if eq(path_of_input, i):
            continue
        else:
            match_input = pre_processing_method(Preprocessing.resize(image_input, 400, 400))
            match_db = pre_processing_method(Preprocessing.resize(cv2.imread(i), 400, 400))
            score_result = Matching.SIFTMatching(match_input, match_db)
            # print(i, " = ", score_result)
            score_total = score_total + score_result
            img_num = img_num + 1
    if img_num == 0:
        print('no image comes in')
        return -1
    else:
        score_average = score_total / img_num
        return score_average


def comparing_result_identification(path_of_input, path_of_database):
    image_input = cv2.imread(path_of_input)
    image_database = []
    image_score = []
    image_dic = {}

    image_database_folder = glob.glob(path_of_database)
    for dog_path in image_database_folder:
        image_database.append(glob.glob(dog_path + '/*.jpg'))
    image_database.sort()

    for dog_datas in image_database:
        score_total = 0
        dog_id = dog_datas[0].split('/')
        for dog_data in dog_datas:
            match_input = pre_processing_method(Preprocessing.resize(image_input, 400, 400))
            match_db = pre_processing_method(Preprocessing.resize(cv2.imread(dog_data), 400, 400))
            score_result = Matching.SIFTMatching(match_input, match_db)
            score_total = score_total + score_result
        image_score.append(score_total)
        image_dic[dog_id[4]] = score_total
        # print('id :', dog_id[4], ' : ', score_total)

    # print('score total : ', image_dic)
    result = ''
    for y, v in sorted(image_dic.items(), key=lambda image_dic: image_dic[1], reverse=True):
        result += y+':'+str(v)+'/'
    return result


if __name__ == "__main__":

    # 4) Recognition
    # Execution example)
    # python DogNoseRecognition inputImage.png dog3

    if len(sys.argv) == 1:
        path_input = "./public/images/data/dog6/1.png"
        path_data = "./public/images/data/dog3/*.png"

    elif len(sys.argv) == 3:
        mode = sys.argv[1]

    else:
        print('error')

    # Mode 1 : Verification
    if eq(mode, '1'):
        path_input = "./public/images/inputimage/"+ sys.argv[2] + ".jpg"
        path_data = "./public/images/dogsnose/" + sys.argv[2] + "/*.jpg"
        average_result = comparing_result_verification(path_input, path_data)
        # print("Average : ", round(average_result, 3))
        if average_result > 50:
            print('true')
        elif average_result == -1:
            print('inputerror')
        else:
            print('false')
        print("Average : ", round(average_result, 3))

    # Mode 2 : Identification
    elif eq(mode, '2'):
        path_input = "./public/images/inputimage/"+ sys.argv[2] + ".jpg"
        path_data = './public/images/dogsnose/*'
        whose_dog = comparing_result_identification(path_input, path_data)
        print(whose_dog)

    cv2.waitKey(0)  # Waits forever for user to press any key
    cv2.destroyAllWindows()  # Closes displayed windows
