# Deploy to Google Cloud Functions

1. in microservice module: `mvn package`
2. `gcloud functions deploy <function-name> --entry-point=com.samvasta.imagegenerator.microservice.gcpfunctions.FuncGenerateSingle --runtime=java11 --trigger-http --source=target/deployment`
