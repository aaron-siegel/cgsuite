#!/bin/bash

# This script assumes there is a file
#
# $basedir/lib/discord-bot/local/deploy.properties
#
# with these lines:
#
# bot.server.addr=<ip address of server> (e.g. "99.99.99.99")
# bot.server.path=<server dir where jar will be stored> (e.g. "/root")
# bot.server.user=<username for server> (e.g. "root")
# bot.discord.key=<discord key>
#
# The server needs to be enabled for ssh; for example (on dev machine):
# ssh-keygen -t dsa			# Enter blank passphrase when prompted
# ssh-copy-id root@99.99.99.99

function prop {
    grep "$1" "$propfile" | cut -d'=' -f2-
}

set -e

basedir=$(dirname "$0")/..
propfile="$basedir/lib/discord-bot/local/deploy.properties"
version=`grep version "$basedir/lib/discord-bot/pom.xml" | head -1 | sed -E 's/.*<version>(.*)<\/version>/\1/'`
server_user="$(prop bot.server.user)"
server_addr="$(prop bot.server.addr)"
server_path="$(prop bot.server.path)"

echo "Deploying discord bot to $(prop bot.server.addr)."
echo "Current library version (from pom.xml) is $version."
echo "Building ..."

(cd "$basedir/lib/discord-bot"; mvn package -f pom.xml)

echo "Copying jar ..."

scp "$basedir/lib/discord-bot/target/cgsuite-discord-bot-$version-jar-with-dependencies.jar" \
    "$server_user"@"$server_addr":"$server_path"

restart_script="$basedir/lib/discord-bot/target/restart-bot-$version.sh"

echo "Generating script at: $restart_script"

cat << EOT >> "$restart_script"
#!/bin/sh
kill \$(pidof java)
nohup java -Xmx1g -cp "cgsuite-discord-bot-$version-jar-with-dependencies.jar" \\
  org.cgsuite.bot.discord.DiscordBot \\
  $(prop bot.discord.key)
EOT

chmod a+x "$restart_script"

scp "$restart_script" "$server_user"@"$server_addr":"$server_path"

echo "Executing script; press Ctrl-C when satisfied ..."

ssh "$server_user"@"$server_addr" "$server_path/restart-bot-$version.sh"
