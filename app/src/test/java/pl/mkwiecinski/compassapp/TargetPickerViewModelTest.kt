package pl.mkwiecinski.compassapp

import org.junit.Test
import pl.mkwiecinski.compassapp.shared.execute
import pl.mkwiecinski.compassapp.shared.validation.ValidationException
import pl.mkwiecinski.compassapp.shared.value
import pl.mkwiecinski.compassapp.vm.TargetPickerViewModel


class TargetPickerViewModelTest {
    @Test fun validate_validData_successCalled() {
        val tested = TargetPickerViewModel()
        tested.latitude.value = "41"
        tested.longitude.value = "11"
        val success = tested.validateCommand.success.test()

        tested.validateCommand.execute()

        success.assertValue { it.latitude == 41.0 && it.longitude == 11.0 }
    }

    @Test fun validate_empty_errorCalled() {
        val tested = TargetPickerViewModel()
        tested.latitude.value = ""
        tested.longitude.value = ""
        val error = tested.validateCommand.error.test()

        tested.validateCommand.execute()

        error.assertValue { it is ValidationException && it.errors.size == 2 }
    }

    @Test fun validate_toBigValue_errorCalled() {
        val tested = TargetPickerViewModel()
        tested.latitude.value = "111"
        tested.longitude.value = "14"
        val error = tested.validateCommand.error.test()

        tested.validateCommand.execute()

        error.assertValue { it is ValidationException && it.errors.size == 1 }
    }
}
