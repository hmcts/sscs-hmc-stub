#HMC to Hearings API
module "servicebus-topic" {
  source                = "git@github.com:hmcts/terraform-module-servicebus-topic?ref=master"
  name                  = "stub-hmc-to-cft-${var.env}"
  namespace_name        = "sscs-servicebus-${var.env}"
  resource_group_name   = "sscs-${var.env}"
}

resource "azurerm_key_vault_secret" "sscs-servicebus-topic-namespace-name" {
  name = "sscs-servicebus-topic-namespace-name"
  value = "sscs-servicebus-${var.env}"
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id
}

resource "azurerm_key_vault_secret" "stub-hmc-servicebus-topic-name" {
  name = "stub-hmc-servicebus-topic-name"
  value = module.servicebus-topic.name
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id
}

resource "azurerm_key_vault_secret" "stub-hmc-servicebus-policy-name" {
  name = "stub-hmc-servicebus-policy-name"
  value = module.send_listen_auth_rule.name
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id
}

resource "azurerm_key_vault_secret" "stub-hmc-servicebus-shared-access-key-tf" {
  name = "mock-hmc-servicebus-shared-access-key-tf"
  value = module.send_listen_auth_rule.primary_key
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id
}

module "servicebus-subscription" {
  source              = "git@github.com:hmcts/terraform-module-servicebus-subscription?ref=master"
  name                = "hmc-to-sscs-subscription-${var.env}"
  namespace_name      = "hmc-servicebus-${var.env}"
  topic_name          = "stub-hmc-to-cft-${var.env}"
  resource_group_name = "hmc-shared-${var.env}"
}

resource "azurerm_key_vault_secret" "stub-hmc-servicebus-subscription-name" {
  name = "stub-hmc-servicebus-subscription-name"
  value = module.servicebus-subscription.name
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id
}
