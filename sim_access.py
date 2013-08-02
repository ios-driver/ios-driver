import re
import shutil
import tempfile
import difflib

# read the contents of /etc/authorization
with open('/etc/authorization','r') as file:
  content = file.read()
match = re.search('<key>system.privilege.taskport</key>\s*\n\s*<dict>\n\s*<key>allow-root</key>\n\s*(<[^>]+>)',content)
if match is None:
	raise Exception('Could not find the system.privilege.taskport key in /etc/authorization')
elif re.search('<false/>', match.group(0)) is None:
	print '/etc/authorization has already been modified'
	exit(0)
new_text = match.group(0).replace(match.group(1), '<true/>')
new_content = content.replace(match.group(0), new_text)

# backup /etc/authorization
backupFile = tempfile.mkstemp('.backup','authorization')[1]
shutil.copy('/etc/authorization', backupFile)
print 'backed up /etc/authorization to %s...' % backupFile

# write back the modified permissions
with open('/etc/authorization','w') as file:
	file.write(new_content)
	print 'wrote new /etc/authorization'
