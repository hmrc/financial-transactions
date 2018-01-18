#!/bin/bash

echo "Applying migration $pluralModel;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$pluralModel;format="decap"$               controllers.$pluralModel$Controller.index" >> ../conf/app.routes

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $pluralModel;format="snake"$ completed"
