package pl.mkwiecinski.compassapp.shared.validation

class ValidationException(val errors: List<ValidationKey>) : Exception()


