import json
import re

emailRegex = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,7}\b'
groups = {}
group = "A"
teacher = "teacher"

while (True):
    teacher = input("Enter the email for the teacher of group " + group + ": ")
    if (teacher != ""):
        if (re.match(emailRegex, teacher) == None):
            print("Invalid email address. Please try again.")
            continue
        groups[group] = teacher
        group = chr(ord(group) + 1)
    else:
        break

with open('groups.json', 'w') as outfile:
    json.dump(groups, outfile)
