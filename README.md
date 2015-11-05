# Elastic Search engine API
build on play framework 2.4 with scala development

IP : 10.97.13.150:9000


## api spec
提供功能, [els api spec overview](/doc/els_api_spec.html)
* search


## Deployment
### package
`activator clean dist`

### run
cp `${project}/target/universal/els_api.zip` to server and unzip

* run pre_production

```
${project}/bin/els_api -Dconfig.resource=pre_prod.conf -Dlogger.resource=pre_prod_logback.xml
```

* run production

```
${project}/bin/els_api -Dconfig.resource=prod.conf -Dlogger.resource=prod_logback.xml
```

* run on 10.97.13.150, simply use shell

```
## pre_production
/home/steven/els_api/run_pre_prod.sh

## production
/home/steven/els_api/run_prod.sh
```
