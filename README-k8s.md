# Peer to Peer Cluster of Homogeneous Service inside Kubernetes Namespace
The project is developed to test out 

Following has to be added to work with Apache Ignite
```
export JAVA_TOOL_OPTIONS="--add-opens=java.base/jdk.internal.access=ALL-UNNAMED \
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED \
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED \
--add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED \
--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED \
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-opens=java.base/java.nio=ALL-UNNAMED \
--add-opens=java.base/java.net=ALL-UNNAMED \
--add-opens=java.base/java.util=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED \
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
--add-opens=java.base/java.math=ALL-UNNAMED \
--add-opens=java.sql/java.sql=ALL-UNNAMED \
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.time=ALL-UNNAMED \
--add-opens=java.base/java.text=ALL-UNNAMED \
--add-opens=java.management/sun.management=ALL-UNNAMED \
--add-opens=java.desktop/java.awt.font=ALL-UNNAMED"
```


# Local Docker Registry


```
export img_tags=latest \
&& export app=p2p \
&& export registry=myregistry.dockerdomain.com:5443
```

# Pushing Application image into local Docker Registry
```
docker build --tag ${app}:${img_tags} . \
&& docker tag ${app}:${img_tags} ${registry}/${app}:${img_tags} \
&& docker push ${registry}/${app}:${img_tags}
```

# Delete existing image

Get the Digest
```
export DIGEST=$(curl --insecure -s -X GET https://$registry/v2/$app/manifests/latest \
  -H "Accept: application/vnd.docker.distribution.manifest.v2+json" | jq -r '.config.digest')
```

Delete using Digest
```
curl --insecure -X DELETE https://$registry/v2/$app/manifests/$DIGEST

curl --insecure -X DELETE \
  -H "Accept: application/vnd.docker.distribution.manifest.v2+json" \
  https://$registry/v2/$app/manifests/$DIGEST

```

Run Garbage Collector to delete unreferenced blobs
```
docker exec -it registry \
bin/registry garbage-collect /etc/docker/registry/config.yml
```


# Kubernetes Deployment Guide

This guide explains how to deploy the p2pcluster service to Kubernetes.

## Deployment Steps
1. Delete all objects of the namespace - `kubectl delete namespace p2p-ns`
2. Create the namespace - `kubectl apply -f k8s/namespace.yaml`
2. Create Cluster Role, Service Account & Role Binding - `kubectl apply -f k8s/cluster-role-service-account-binding.yaml`
3. Deploy the NGINX ingress controller - `kubectl apply -f k8s/ingress-controller.yaml`
4. Deploy the application - `kubectl apply -f k8s/deployment.yaml`
5. Deploy the service - `kubectl apply -f k8s/service.yaml`
6. Deploy the ingress - `kubectl apply -f k8s/ingress.yaml`

# Ignite Cluster Topology snapshot
```
kubectl get pods -n p2p-ns -o name | xargs -I{} kubectl logs -n p2p-ns {} | grep "Topology snapshot"
```

```
kubectl get pods -n p2p-ns -o name | xargs -I{} kubectl logs -n p2p-ns -f {} | grep "At Node:"
```

## Configuration Notes
- The service is deployed to the `p2pcluster-ns` namespace
- The deployment runs 2 replicas of the application
- Resource limits are set to:
  - Memory: 512Mi (request) / 1Gi (limit)
  - CPU: 500m (request) / 1000m (limit)
- The service is exposed through an NGINX ingress controller
- The application is accessible through the domain: p2pcluster.example.com (change this in ingress.yaml)

## Verification

Check the deployment status:
```bash
kubectl get all -n p2pcluster-ns
kubectl get ingress -n p2pcluster-ns
```

## Important

- Update the host in ingress.yaml to match your domain
- Ensure your Kubernetes cluster has the necessary resources
- You may need to adjust resource limits based on your application's needs
- Build and push the Docker image before deployment