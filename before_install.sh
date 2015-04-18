echo -e ">>> Authentication !"
 
git remote set-url origin git@github.com:clairton/vraptor-crud.git
git config --global user.email "clairton.rodrigo@gmail.com"
git config --global user.name "Travis CI"
 
echo "\$env.PRIVATE_KEY" > ~/.ssh/id_rsa
 
chmod 600 ~/.ssh/id_rsa

echo -e ">>> Hi github.com !"
ssh -T git@github.com
echo -e "\n"
