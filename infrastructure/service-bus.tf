#HMC to Hearings API
module "servicebus-subscription" {
  source              = "git@github.com:hmcts/terraform-module-servicebus-subscription?ref=master"
  name                = "mock-hmc-to-sscs-subscription-${var.env}"
  namespace_name      = "sscs-servicebus-${var.env}"
  topic_name          = "mock-hmc-to-cft-${var.env}"
  resource_group_name = "sscs-${var.env}"
}
