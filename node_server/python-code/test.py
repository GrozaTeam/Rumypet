import sys

argv_length = sys.argv
print('argv length' + argv_length)
if argv_length == 1:
    print('please input image')
elif argv_length == 2:
    print('input image is ' + sys.argv[1])
else:
    print('wtf?')
