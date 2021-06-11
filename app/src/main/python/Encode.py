import cv2
import numpy as np
from PIL import Image
from array import array
from numpy import array
import os
import requests
import math
import sys

AP_IP = "192.168.4.1"
File_Name = "c.gif"
Motor_Speed = 0.1
SizeIMG = 200
ChannelVideo = 4


def CropImage(cropimage, h, w):
    if h < w:
        x1 = int((w-h)/2)
        y1 = int(1)
        x2 = int((w+h)/2)
        y2 = int(h)
        if x2 - x1 > y2 - y1:
            x2 = x1 + y2 - y1
        else:
            y2 = y1 + x2 - x1
        crop = cropimage[y1:y2, x1:x2]
    elif h > w:
        x1 = int(0)
        y1 = int((h-w)/2)
        x2 = int(w)
        y2 = int((h+w)/2)
        if x2 - x1 > y2 - y1:
            x2 = x1 + y2 - y1
        else:
            y2 = y1 + x2 - x1
        crop = cropimage[y1:y2, x1:x2]
    elif h == w:
        crop = cropimage
    return crop


def ResizeImage(resizeimage, size):
    dim = (size, size)
    resized = cv2.resize(resizeimage, dim, interpolation=cv2.INTER_AREA)
    return resized


def resizeImage(frame, Image_size):
    frame_size = frame.shape
    convert_size = min(frame_size[0], frame_size[1])
    from_w = int((frame_size[1] - convert_size)/2)
    to_w = int((frame_size[1] + convert_size)/2)
    from_h = int((frame_size[0] - convert_size)/2)
    to_h = int((frame_size[0] + convert_size)/2)
    cropped_frame = frame[from_h:to_h, from_w:to_w]
    resize_to = (Image_size, Image_size)
    resized_frame = cv2.resize(
        cropped_frame, resize_to, interpolation=cv2.INTER_AREA)
    # print(resized_frame.shape)
    return resized_frame


def SaveFrame(saveimage, filename):
    data_array = saveimage.flatten()

    converted_data = np.asarray(data_array, dtype=np.uint8)

    # np.savetxt("Data.txt", converted_data, fmt='%d', delimiter=', ')
    converted_data.astype('uint8').tofile(filename)
    # saveimage
    ListData = []
    color = saveimage.flatten()
    ListData = color.tobytes()
    with open(filename, 'wb') as file:
        for i in ListData:
            file.write(i.to_bytes(1, byteorder='big'))


def ProccessDataVideo(giffilename):
    # gif = cv2.VideoCapture(giffilename)
    cap = cv2.VideoCapture(giffilename)
    length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    fps = cap.get(cv2.CAP_PROP_FPS)
    data_text = []
    frame = Image.open(giffilename)
    if fps > 10:
        fps = 10
    nframes = 0
    ListData = []
    while frame:
        nframes += 1
        pil_frame = frame.convert('RGB')
        np_frame = np.array(pil_frame)
        cv2_frame = cv2.cvtColor(np_frame, cv2.COLOR_RGB2BGR)
        cv2_frame = cv2_frame.astype(np.uint8)
        resized_img = resizeImage(cv2_frame, SizeIMG)

        data_array = resized_img.flatten()
        ListData.clear()
        for i in range(0, SizeIMG):
            for j in range(0, SizeIMG):
                ListData.append(120)
                ListData.append(resized_img[i, j][0])
                ListData.append(resized_img[i, j][1])
                ListData.append(resized_img[i, j][2])

        converted_data = np.asarray(ListData, dtype=np.uint8)
        converted_data.astype('uint8').tofile(str(nframes) + '.bin')
        
        if (nframes > length+1):
            break
        try:
            frame.seek(nframes)
        except EOFError:
            break
    return nframes, fps


def SendFile(IP_address, file_path):
    myfile = open(file_path, 'rb')
    myurl = 'http://' + IP_address + '/fupload'
    r = requests.post(myurl, files={'file': myfile})
    print("Done Send" + file_path)


def SetParameter(IP_address, numberframe, fps, speed, sizeimg, channel):
    parameterdata = {
        "Number-Frame": str(numberframe), "FPS-Video": str(fps), "Size-Image": str(sizeimg), "Channel-Image": str(channel), "Speed-Motor": str(speed)}
    myurl = 'http://' + IP_address + '/Parameter'
    r = requests.post(myurl, data=parameterdata)
    print(r.text)
    if r.text == "Success!":
        return True
    else:
        return False


def SetSpeed(IP_address, speed):
    myurl = 'http://' + IP_address + '/'
    mydata = {"Set-Speed": str(speed)}
    r = requests.post(myurl, data=mydata)
    print(r.text)


def GetFPS(IP_address):
    mydata = {'Check': 'FPS'}
    myurl = 'http://' + IP_address + '/'
    r = requests.get(myurl, params=mydata)
    print(r.text)


def GetNumberFrame(IP_address):
    mydata = {'Check': 'NumberFrame'}
    myurl = 'http://' + IP_address + '/'
    r = requests.get(myurl, params=mydata)
    print(r.text)


def GetSpeed(IP_address):
    mydata = {'Check': 'Motor-Speed'}
    myurl = 'http://' + IP_address + '/'
    r = requests.get(myurl, params=mydata)
    print(r.text)


def RunFAN(IP_address):
    runfandata = {'Status': 'Run-Fan!'}
    myurl = 'http://' + IP_address + '/'
    r = requests.post(myurl, data=runfandata)
    print(r.text)


def RunOldData(IP_address):
    runfandata = {'Status': 'Run-OldData!'}
    myurl = 'http://' + IP_address + '/'
    r = requests.post(myurl, data=runfandata)
    print(r.text)


def SendAllBinFile(IP_address, numberframe):
    for i in range(0, numberframe):
        filename = str(i + 1) + ".bin"
        SendFile(IP_address, filename)
        print("Done Send" + filename)


def DeleteFile(numberframe):
    for i in range(0, numberframe):
        filename = str(i + 1) + ".bin"
        os.remove(filename)


if __name__ == "__main__":
    Number_Frame, Video_FPS = ProccessDataVideo(File_Name)
    
    # Number_Frame = 20
    # Motor_Speed = 0.5
    # # Number_Frame = 1
    # Video_FPS = 1
    # SetParameter(AP_IP, Number_Frame, Video_FPS, Motor_Speed, SizeIMG, ChannelVideo)
    # # DeleteFile(Number_Frame)
    # RunFAN(AP_IP)



    # SendAllBinFile(AP_IP, Number_Frame)
    # DeleteFile(Number_Frame)
    # RunFAN(AP_IP)
    # RunOldData(AP_IP)
