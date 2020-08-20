package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard.CustomRecipe

interface RecipeProvider {
    fun getRecipe(slot: Int): CustomRecipe
}