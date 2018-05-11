import sys

print(sys.version)
argv_length = len(sys.argv)
print('argv length', argv_length)
if argv_length == 1:
    print('please input image')
elif argv_length == 2:
    print('input image is ' + sys.argv[1])
elif argv_length == 3:
    print('input image is' + sys.argv[1] + ' : ' + sys.argv[2])
else:
    print('wtf?')
