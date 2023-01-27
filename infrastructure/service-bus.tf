#HMC to Hearings API
module "servicebus-topic" {
  source                = "git@github.com:hmcts/terraform-module-servicebus-topic?ref=master"
  name                  = "${SERVICE_NAME}-stub-hmc-to-cft-${var.env}"
  namespace_name        = "sscs-servicebus-${var.env}"
  resource_group_name   = "sscs-${var.env}"
}
