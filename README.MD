`build -t phoosop/reactive .`

`docker run -d --cpus="2" --name reactive_java8 -p 8080:8080 -p 9010:9010 phoosop/reactive`