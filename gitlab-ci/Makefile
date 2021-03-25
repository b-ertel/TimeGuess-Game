TARGET_REPOSITORY=informatik/qe/sepsss21/group1/g1t1

# Login and build docker image
docker-build:
	docker login docker.uibk.ac.at:443
	docker build -t docker.uibk.ac.at:443/${TARGET_REPOSITORY} .

# Upload docker images to Gitlab registry
docker-push:
	docker push docker.uibk.ac.at:443/${TARGET_REPOSITORY}

# Run locally
docker-run:
	docker run -it docker.uibk.ac.at:443/${TARGET_REPOSITORY} bash
