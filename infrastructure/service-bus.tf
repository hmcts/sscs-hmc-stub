#HMC to Hearings API
module "servicebus-topic" {
  source                = "git@github.com:hmcts/terraform-module-servicebus-topic?ref=master"
  name                  = "stub-hmc-to-cft-${var.env}"
  resource_group_name   = "sscs-${var.env}"
  namespace_name        = "sscs-servicebus-${var.env}"
}

resource "azurerm_key_vault_secret" "sscs-servicebus-topic-namespace-name" {
  name = "sscs-servicebus-topic-namespace-name"
  value = "sscs-servicebus-${var.env}"
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

    content_type = "secret"
    tags = merge(var.common_tags, {
      "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
    })
}

resource "azurerm_key_vault_secret" "stub-servicebus-topic-name" {
  name = "stub-servicebus-topic-name"
  value = module.servicebus-topic.name
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

    content_type = "secret"
    tags = merge(var.common_tags, {
      "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
    })
}

resource "azurerm_key_vault_secret" "stub-servicebus-policy-name" {
  name = "stub-servicebus-policy-name"
  value = "SendAndListenSharedAccessKey"
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

    content_type = "secret"
    tags = merge(var.common_tags, {
      "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
    })
}

resource "azurerm_key_vault_secret" "stub-servicebus-shared-access-key-tf" {
  name = "stub-servicebus-shared-access-key-tf"
  value = module.servicebus-topic.primary_send_and_listen_shared_access_key
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

    content_type = "secret"
    tags = merge(var.common_tags, {
      "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
    })
}

module "servicebus-subscription" {
  source              = "git@github.com:hmcts/terraform-module-servicebus-subscription?ref=master"
  name                = "stub-to-sscs-subscription-${var.env}"
  resource_group_name = "sscs-${var.env}"
  namespace_name      = "sscs-servicebus-${var.env}"
  topic_name          = "stub-hmc-to-cft-${var.env}"

  depends_on = [module.servicebus-topic]
}

resource "azurerm_key_vault_secret" "stub-servicebus-subscription-name" {
  name = "stub-servicebus-subscription-name"
  value = "stub-to-sscs-subscription-${var.env}"
  key_vault_id = data.azurerm_key_vault.sscs_key_vault.id

    content_type = "secret"
    tags = merge(var.common_tags, {
      "source" : "Vault ${data.azurerm_key_vault.sscs_key_vault.name}"
    })
}
