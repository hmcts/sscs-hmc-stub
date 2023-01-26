#HMC to Hearings API
module "servicebus-subscription" {
  source              = "git@github.com:hmcts/terraform-module-servicebus-subscription?ref=master"
  name                = "mock-hmc-to-sscs-subscription-${var.env}"
  namespace_name      = "sscs-servicebus-${var.env}"
  topic_name          = "mock-hmc-to-cft-${var.env}"
  resource_group_name = "sscs-${var.env}"
}

resource "azurerm_key_vault_secret" "mock-hmc-servicebus-shared-access-key-tf" {
  name         = "mock-hmc-servicebus-shared-access-key-tf"
  value        = data.azurerm_key_vault_secret.mock-hmc-servicebus-shared-access-key.value
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

  content_type = "secret"
  tags = merge(var.common_tags, {
    "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
  })
}
