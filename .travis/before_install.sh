echo -e ">>> Authentication !"
 
git remote set-url origin git@github.com:clairton/vraptor-crud.git
git config --global user.email "clairton.rodrigo@gmail.com"
git config --global user.name "Travis CI"
 
cat > ~/.ssh/id_rsa << EOL
$env.PRIVATE_KEY
EOL

chmod 600 ~/.ssh/id_rsa

echo -e ">>> Hi github.com !"
ssh -T git@github.com
echo -e "\n"
