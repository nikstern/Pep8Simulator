

public class Simulator {

    public static int[] inputBytes = {0x04, 0x00, 0x18, 0x7f, 0xff, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0d, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0xc1, 0x00, 0x03, 0x22, 0x20, 0x1c, 0x1e, 0x1a, 0x18, 0xa0, 0xff, 0xff, 0x90, 0x00, 0x00, 0x70, 0x00, 0x01, 0x81, 0x00, 0x05, 0xf1, 0x00, 0x07, 0xd1, 0x00, 0x07, 0xc9, 0x00, 0x09, 0x72, 0x00, 0x0b, 0x85, 0x00, 0x0d, 0xe1, 0x00, 0x07, 0x78, 0x00, 0x01, 0xe9, 0x00, 0x09, 0xc5, 0x00, 0x0b, 0xb8, 0x00, 0x02, 0x08, 0x00, 0x30, 0x06, 0x00, 0x30, 0x0a, 0x00, 0x30, 0x00, 0x00};
    public static boolean nFlag = false;
    public static boolean zFlag = false;
    public static boolean vFlag = false;
    public static boolean cFlag = false;
    public static int accumulator = 0;
    public static int indexRegister = 0;
    public static int programCounter = 0;
    public static int instructionSpecifier = -1;
    public static int operandSpecifier = 0;
    public static int operand = 0;
    public static char register = ' ';
    public static char addressingMode = ' ';
    public static int intermediateOperand = 0;
    public static boolean isUnary = true;
    // CONSTANTS SECTION
    // INSTRUCTION CATEGORY BOUNDS
    public static final int UNARY_VALUE = 4;
    public static final int SINGLE_ADDRESS_VALUE = 24;
    public static final int SINGLE_REGISTER_VALUE = 36;
    // BIT SHIFT VALUES
    public static final int SIXTEEN_ON_BITS = 0xFFFF;
    public static final int MAX_PEP_VALUE = 32768;
    public static final int EIGHT_BIT_SHIFT = 256;
    public static final int FOUR_BIT_SHIFT = 16;
    public static final int THREE_BIT_SHIFT = 8;
    public static final int ONE_BIT_SHIFT = 2;
    public static final int FIRST_BIT_ON = 0x8000;
    public static final int SECOND_HALF_OPERAND_BITS = 0x00FF;
    // REGISTER ADDRESS INSTRUCTION INDICES
    public static final int ADD_VALUE = 7;
    public static final int SUB_VALUE = 8;
    public static final int AND_VALUE = 9;
    public static final int OR_VALUE = 10;
    public static final int CP_VALUE = 11;
    public static final int LD_VALUE = 12;
    public static final int LDBYTE_VALUE = 13;
    public static final int ST_VALUE = 14;
    public static final int STBYTE_VALUE = 15;
    // SINGLE REGISTER INSTRUCTION INDICES
    public static final int NOT_VALUE = 12;
    public static final int NEG_VALUE = 13;
    public static final int ASL_VALUE = 14;
    public static final int ASR_VALUE = 15;
    public static final int ROL_VALUE = 16;
    public static final int ROR_VALUE = 17;
    // SINGLE ADDRESS INSTRUCTION INDICES
    public static final int BR_VALUE = 2;
    public static final int BRLE_VALUE = 3;
    public static final int BRLT_VALUE = 4;
    public static final int BREQ_VALUE = 5;
    public static final int BRNE_VALUE = 6;
    public static final int BRGE_VALUE = 7;
    public static final int BRGT_VALUE = 8;
    public static final int BRV_VALUE = 9;
    public static final int BRC_VALUE = 10;
    // UNARY INSTRUCTION INDICES
    public static final int STOP_VALUE = 0;
    // NUMBER OF BITS IDENTIFIERS
    public static final int SINGLE = 1;
    public static final int DOUBLE = 2;
    public static final int TRIPLE = 3;
    // REGISTER CODES
    public static final int ACCUMULATOR_SPECIFIER = -1;
    public static final int INDEX_REGISTER_SPECIFIER = -2;
    public static final char ACCUMULATOR_CHAR = 'A';
    public static final char INDEX_REGISTER_CHAR = 'X';
    // ADDRESSING MODE STRING VALUES
    public static final char IMMEDIATE_ADDRESSING = 'i';
    public static final char DIRECT_ADDRESSING = 'd';
    public static final char INDIRECT_ADDRESSING = 'n';
    public static final char INDEXED_ADDRESSING = 'x';
    // ADDRESSING MODE NUMERIC VALUES
    public static final int IMMEDIATE_VALUE = 0;
    public static final int DIRECT_VALUE = 1;
    public static final int INDIRECT_VALUE = 2;
    public static final int INDEXED_VALUE = 5;


    public static void main(String[] args) {
        System.out.println("Start of CPUSIM");
        DisplayInput();
        VonNeumannCycle();
    }
    public static void DisplayInput()
    {
        System.out.println("Displaying the program in hexadecimal that the program has to execute as is stored in memory starting at location 0");
        System.out.println();
        for (int i = 0; i < inputBytes.length; i++)
        {
            System.out.printf("%02x ", inputBytes[i]);
            if (i % 20 == 19)
            System.out.println();
        }
        System.out.println();
    }
    public static void VonNeumannCycle()
    {
       while (instructionSpecifier != STOP_VALUE)
       {

       FetchInstruction();
       DisplayFetchedInstruction();
       DecodeInstruction();
       }
    }
    public static void FetchInstruction()
    {
      instructionSpecifier = inputBytes[programCounter];
      UpdatePC(SINGLE);
    }
    public static void DecodeInstruction()
    {
      if (instructionSpecifier < UNARY_VALUE)
      {
           ExecuteUnaryInstruction();
      }
      else if (instructionSpecifier < SINGLE_ADDRESS_VALUE)
      {
           GetAddressingMode(SINGLE);
           FetchOperandSpecifierFromInput();
           FetchOperandsSingleAddressInstruction();
           ExecuteSingleAddressInstruction();
      }
      else if (instructionSpecifier < SINGLE_REGISTER_VALUE)
      {
           GetRegisterFromInstruction(instructionSpecifier);
           FetchOperandsSingleRegisterInstruction();
           ExecuteSingleRegisterInstruction();
      }
      else
      {
           GetRegisterFromInstruction(instructionSpecifier / THREE_BIT_SHIFT);
           FetchOperandSpecifierFromInput();
           GetAddressingMode(TRIPLE);
           ExecuteRegisterAddressInstruction();
      }
    }
    public static void FetchOperandsSingleAddressInstruction()
    {
        if (addressingMode == (IMMEDIATE_ADDRESSING))
        {
            operand = operandSpecifier;
        }
        else
        {
            intermediateOperand = operandSpecifier + indexRegister;
            CalculateOperand(operandSpecifier+indexRegister);
        }
    }
    public static void FetchOperandsSingleRegisterInstruction()
    {
     if (register == ACCUMULATOR_CHAR)
     {
         operandSpecifier = ACCUMULATOR_SPECIFIER;
         operand = accumulator;
     }
     else
     {
         operandSpecifier = INDEX_REGISTER_SPECIFIER;
         operand = indexRegister;
     }

    }


    public static void GetAddressingMode(int numberOfBits)
    {
    if (numberOfBits == TRIPLE)
    {
        GetThreeBitAddressingMode();
    }
    else
    {
        addressingMode = instructionSpecifier % ONE_BIT_SHIFT == 0 ? IMMEDIATE_ADDRESSING : INDEXED_ADDRESSING;
    }
    }
    public static void GetRegisterFromInstruction(int number)
    {
      register = number % ONE_BIT_SHIFT == 0 ? ACCUMULATOR_CHAR : INDEX_REGISTER_CHAR;
    }
    public static void GetThreeBitAddressingMode()
    {

        switch(instructionSpecifier % THREE_BIT_SHIFT)
        {

            case IMMEDIATE_VALUE:
                addressingMode = IMMEDIATE_ADDRESSING;
                operand = operandSpecifier;
                break;
            case DIRECT_VALUE:
                addressingMode = DIRECT_ADDRESSING;
                CalculateOperand(operandSpecifier);
                break;
            case INDIRECT_VALUE:
                addressingMode = INDIRECT_ADDRESSING;
                CalculateOperand(operandSpecifier);
                intermediateOperand = operand;
                CalculateOperand(operand);
                break;
            case INDEXED_VALUE:
                addressingMode = INDEXED_ADDRESSING;
                intermediateOperand = operandSpecifier + indexRegister;
                CalculateOperand(operandSpecifier + indexRegister);
                break;
            default:
                addressingMode = ' ';
                break;
        }
    }
    public static void FetchOperandSpecifierFromInput()
    {
        operandSpecifier = EIGHT_BIT_SHIFT * inputBytes[programCounter] + inputBytes[programCounter+1];
        UpdatePC(2);
    }
    public static void CalculateOperand(int index)
    {
        operand = EIGHT_BIT_SHIFT * inputBytes[index] + inputBytes[index+1];
    }
    public static void ExecuteUnaryInstruction()
    {
       switch(instructionSpecifier)
       {
           case STOP_VALUE:
               Stop();
               break;
           default:
               break;
       }
    }


    public static void Stop()
    {
          System.out.println("Instruction name: STOP, OPcode is unary");
    }
    public static void ExecuteSingleAddressInstruction()
    {
        int index = instructionSpecifier / DOUBLE;
        isUnary = false;
        switch (index)
        {
            case BR_VALUE:
                BR();
                break;
            case BRLE_VALUE:
                BRLE();
                break;
            case BRLT_VALUE:
                BRLT();
                break;
            case BREQ_VALUE:
                BREQ();
                break;
            case BRNE_VALUE:
                BRNE();
                break;
            case BRGE_VALUE:
                BRGE();
                break;
            case BRGT_VALUE:
                BRGT();
                break;
            case BRV_VALUE:
                BRV();
                break;
            case BRC_VALUE:
                BRC();
                break;
            default:
                break;
        }


    }
    public static void BR()
    {
        System.out.println("Instruction name: BR, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        programCounter = operand;
    }
    public static void BRLE()
    {
       System.out.println("Instruction name: BRLE, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
       if (nFlag || zFlag)
       {
           System.out.println("cond Branch TAKEN!");
           programCounter = operand;
       }
       else
       {
           System.out.println("cond Branch NOT taken!");
       }
    }
    public static void BRLT()
    {
        System.out.println("Instruction name: BRLT, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (nFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }
    public static void BREQ()
    {
        System.out.println("Instruction name: BREQ, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
         if (zFlag)
         {
         System.out.println("cond Branch TAKEN!");
         programCounter = operand;
         }
         else
         {
             System.out.println("cond Branch NOT taken!");
         }
    }
    public static void BRNE()
    {
        System.out.println("Instruction name: BRNE, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (!zFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }
    public static void BRGE()
    {
        System.out.println("Instruction name: BRGE, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (!nFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }
    public static void BRGT()
    {
        System.out.println("Instruction name: BRGT, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (!nFlag && !zFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }
    public static void BRV()
    {
        System.out.println("Instruction name: BRV, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (vFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }
    public static void BRC()
    {
        System.out.println("Instruction name: BRC, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        if (cFlag)
        {
        System.out.println("cond Branch TAKEN!");
        programCounter = operand;
        }
        else
        {
            System.out.println("cond Branch NOT taken!");
        }
    }

    public static void ExecuteSingleRegisterInstruction()
    {
        int index = instructionSpecifier / DOUBLE;
        isUnary = true;
        switch (index)
        {
            case NOT_VALUE:
                NOT();
                break;
            case NEG_VALUE:
                NEG();
                break;
            case ASL_VALUE:
                ASL();
                break;
            case ASR_VALUE:
                ASR();
                break;
            case ROL_VALUE:
                ROL();
                break;
            case ROR_VALUE:
                ROR();
                break;
            default:
                break;
        }

    }
    public static void NOT()
    {
        int output = operand;
        output = ~output & SIXTEEN_ON_BITS; // remove all bits outside of the range of SIXTEEN_ON_BITS
        zFlag = output == 0;
        nFlag = output >= MAX_PEP_VALUE;
        System.out.print("Instruction name: NOT");
        DisplaySingleRegisterInstruction(output);
    }
    public static void NEG()
    {
        int output = operand;
        output = ((~output) + 1);
        output &= SIXTEEN_ON_BITS;
        zFlag = output == 0;
        nFlag = output >= MAX_PEP_VALUE;  // negative numbers are stored as numbers greater than MAX_PEP_VALUE
        if (operand >= MAX_PEP_VALUE)
        {
            vFlag = output >= MAX_PEP_VALUE;
        }
        else
        {
            vFlag = output < MAX_PEP_VALUE;
        }
        System.out.print("Instruction name: NEG");
        DisplaySingleRegisterInstruction(output);
    }
    public static void ASL()
    {
        int output = operand;
        output *= ONE_BIT_SHIFT;


        if (output >= SIXTEEN_ON_BITS)
        {
            cFlag = true;
            output &= SIXTEEN_ON_BITS; // remove all bits outside of the range of SIXTEEN_ON_BITS
        }
        else
        {
            cFlag = false;
        }
        if (operand >= MAX_PEP_VALUE)
        {
            vFlag = output < MAX_PEP_VALUE;
        }
        else
        {
            vFlag = output >= MAX_PEP_VALUE;
        }
        zFlag = output == 0;
        nFlag = output >= MAX_PEP_VALUE;
        System.out.print("Instruction name: ASL");
        DisplaySingleRegisterInstruction(output);
    }
    public static void ASR()
    {
        int output = operand;
        cFlag = output % ONE_BIT_SHIFT == 1;
        if (output < MAX_PEP_VALUE)
        output /= ONE_BIT_SHIFT;
        else
        output = (output / ONE_BIT_SHIFT) + MAX_PEP_VALUE; // otherwise it would lose half of MAX_PEP_VALUE since it is a "negative" number.
        zFlag = output == 0;
        nFlag = output >= MAX_PEP_VALUE; // negative numbers are stored as numbers greater than MAX_PEP_VALUE
        System.out.print("Instruction name: ASR");
        DisplaySingleRegisterInstruction(output);
    }

    public static void ROL()
    {
        int output = operand;
        output = (output * ONE_BIT_SHIFT);
        output &= SIXTEEN_ON_BITS;
        output |= cFlag ? 1 : 0;
        cFlag = operand >= MAX_PEP_VALUE;
        System.out.print("Instruction name: ROL");
        DisplaySingleRegisterInstruction(output);
    }
    public static void ROR()
    {
        int output = operand;
        output = output / ONE_BIT_SHIFT;
        output |= (cFlag ? FIRST_BIT_ON : 0);
        cFlag = operand % ONE_BIT_SHIFT == 1;
        System.out.print("Instruction name: ROR");
        DisplaySingleRegisterInstruction(output);

    }
    public static void DisplaySingleRegisterInstruction(int output)
    {
        if (register == ACCUMULATOR_CHAR)
        {
            System.out.print("A, OPcode is unary\n");
            accumulator = output;
            System.out.printf("After execution: A= %04x ,", accumulator);
            WriteNFlags();
            System.out.println("\n");
        }
        else
        {
            System.out.print("X, OPcode is unary\n");
            indexRegister = output;
            System.out.printf("After execution: X= %04x ,", indexRegister);
            WriteNFlags();
            System.out.println("\n");
        }
    }
    public static void ExecuteRegisterAddressInstruction()
    {
        int index = instructionSpecifier / FOUR_BIT_SHIFT;
        isUnary = false;
        switch (index)
        {
            case ADD_VALUE:
                ADD();
                break;
            case SUB_VALUE:
                SUB();
                break;
            case AND_VALUE:
                AND();
                break;
            case OR_VALUE:
                OR();
                break;
            case CP_VALUE:
                CP();
                break;
            case LD_VALUE:
                LD();
                break;
            case LDBYTE_VALUE:
                LDBYTE();
                break;
            case ST_VALUE:
                ST();
                break;
            case STBYTE_VALUE:
                STBYTE();
                break;
        }
       System.out.println();
    }
    public static void ADD()
    {
        int registerValue;
        if (register == ACCUMULATOR_CHAR) registerValue = accumulator;
        else registerValue = indexRegister;
        int output = operand + registerValue;
        if (output >= SIXTEEN_ON_BITS + 1)
        {
           cFlag = true;
           output = output & SIXTEEN_ON_BITS;
        }
        else
        {
            cFlag = false;
        }
        nFlag = output >= MAX_PEP_VALUE;
        zFlag = output == 0;
        if (operand >= MAX_PEP_VALUE && registerValue >= MAX_PEP_VALUE)
        {
            vFlag = output < MAX_PEP_VALUE;
        }
        else if (operand < MAX_PEP_VALUE && registerValue < MAX_PEP_VALUE)
        {
           vFlag = output >= MAX_PEP_VALUE;
        }
        else
        {
            vFlag = false;
        }
        System.out.print("Instruction name: ADD");
        EndRegisterAddressInstruction(output);
    }
    public static void SUB()
    {
        int output2 = operand;
        output2 = ~output2 + 1;
        output2 = output2 & SIXTEEN_ON_BITS;
        int registerValue;
        if (register == ACCUMULATOR_CHAR) registerValue = accumulator;
        else registerValue = indexRegister;
        int output = output2 + registerValue;
        if (output >= SIXTEEN_ON_BITS+1)
        {
            cFlag = true;
            output = output & SIXTEEN_ON_BITS;
        }
        else
        {
            cFlag = false;
        }
        nFlag = output >= MAX_PEP_VALUE;
        zFlag = output == 0;
        if (output2 >= MAX_PEP_VALUE && registerValue >= MAX_PEP_VALUE)
        {
            vFlag = output < MAX_PEP_VALUE;
        }
        else if (output2 < MAX_PEP_VALUE && registerValue < MAX_PEP_VALUE)
        {
            vFlag = output >= MAX_PEP_VALUE;
        }
        else
        {
            vFlag = false;
        }
        System.out.print("Instruction name: SUB");
        EndRegisterAddressInstruction(output);

    }
    public static void AND()
    {
        int registerValue;
        if (register == ACCUMULATOR_CHAR) registerValue = accumulator;
        else registerValue = indexRegister;
        int output = registerValue & operand;
        nFlag = output < 0;
        zFlag = output == 0;
        System.out.print("Instruction name: AND");
        EndRegisterAddressInstruction(output);


    }
    public static void OR()
    {
        int registerValue;
        if (register == ACCUMULATOR_CHAR) registerValue = accumulator;
        else registerValue = indexRegister;
        int output = registerValue | operand;
        System.out.print("Instruction name: OR");
        nFlag = output >= MAX_PEP_VALUE;
        zFlag = output == 0;
        EndRegisterAddressInstruction(output);
    }
    public static void CP()
    {
          // unsure if there is anything else that needs to be done.
        int output2 = operand;
        output2 = ~output2 + 1;
        output2 = output2 & SIXTEEN_ON_BITS;
        int registerValue;
        if (register == ACCUMULATOR_CHAR) registerValue = accumulator;
        else registerValue = indexRegister;
        int output = registerValue + output2;
        if (output >= SIXTEEN_ON_BITS+1)
        {
            cFlag = true;
            output =  output & SIXTEEN_ON_BITS;
        }
        else
        {
            cFlag = false;
        }
        nFlag = output >= MAX_PEP_VALUE;
        zFlag = output == 0;
        if (output2 >= MAX_PEP_VALUE && registerValue >= MAX_PEP_VALUE)
        {
            vFlag = output < MAX_PEP_VALUE;
        }
        else if (output2 < MAX_PEP_VALUE && registerValue < MAX_PEP_VALUE)
        {
            vFlag = output >= MAX_PEP_VALUE;
        }
        else
        {
            vFlag = false;
        }
        System.out.print("Instruction name: CP");
        if (register == ACCUMULATOR_CHAR)
        {
            System.out.println("A, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: A= %04x ,", accumulator);

            WriteNFlags();

        }
        else
        {
            System.out.println("X, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: X= %04x ,", indexRegister);
            WriteNFlags();

        }
    }
    public static void LD()
    {
        int output = operand;
        nFlag =operand >= MAX_PEP_VALUE;
        zFlag = operand == 0;
        System.out.print("Instruction name: LD");
        EndRegisterAddressInstruction(output);
    }
    public static void LDBYTE()
    {
        if (addressingMode == IMMEDIATE_ADDRESSING)
        {
        operand = operand / EIGHT_BIT_SHIFT;
        }
        else
        {
        operand = inputBytes[operandSpecifier];
        }

        System.out.print("Instruction name: LDBYTE");
        if (register == ACCUMULATOR_CHAR)
        {
            int firstByte = accumulator / EIGHT_BIT_SHIFT;
            accumulator = EIGHT_BIT_SHIFT * firstByte + operand;
            System.out.println("A, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: A= %04x ,", accumulator);
            nFlag = accumulator >= MAX_PEP_VALUE;
            zFlag = accumulator == 0;
            WriteNFlags();

        }
        else
        {
            int firstByte = indexRegister / EIGHT_BIT_SHIFT;
            indexRegister = FOUR_BIT_SHIFT * firstByte + operand;;
            System.out.println("X, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: X= %04x ,", indexRegister);
            nFlag = indexRegister >= MAX_PEP_VALUE;
            zFlag = indexRegister == 0;
            WriteNFlags();

        }
    }
    public static void ST()
    {
        if (register == ACCUMULATOR_CHAR)
        {
            System.out.println("Instruction name: STA, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();

            inputBytes[operandSpecifier] = accumulator / EIGHT_BIT_SHIFT;
            inputBytes[operandSpecifier+1] = accumulator % EIGHT_BIT_SHIFT;
            int value = EIGHT_BIT_SHIFT * inputBytes[operandSpecifier] + inputBytes[operandSpecifier+1];
            System.out.printf("After execution: memloc=%04x has=%04x\n", operandSpecifier, value);
            System.out.printf("Stored operand data: %04x\n", value);
        }
        else
        {
            System.out.println("Instruction name: STX, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            inputBytes[operandSpecifier] = indexRegister / EIGHT_BIT_SHIFT;
            inputBytes[operandSpecifier+1] = indexRegister % EIGHT_BIT_SHIFT;
            int value = EIGHT_BIT_SHIFT * inputBytes[operandSpecifier] + inputBytes[operandSpecifier+1];
            System.out.printf("After execution: memloc=%04x has=%04x\n", operandSpecifier, value);
            System.out.printf("Stored operand data: %04x\n", value);
        }
    }
    public static void STBYTE()
    {
        if (register == ACCUMULATOR_CHAR)
        {
        System.out.println("Instruction name: STBYTEA, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        inputBytes[operandSpecifier] = (accumulator & SECOND_HALF_OPERAND_BITS);
        System.out.printf("After execution: memloc=%04x has=%02x\n", operandSpecifier, inputBytes[operandSpecifier]);
        System.out.printf("Stored operand data: %02x\n", inputBytes[operandSpecifier]);
        }
        else
        {
        System.out.println("Instruction name: STBYTEX, OPcode is NOT unary");
        WriteOperandSpecifierInformation();
        WriteAddressingModeInformation();
        inputBytes[operandSpecifier] = (indexRegister & SECOND_HALF_OPERAND_BITS);
        System.out.printf("After execution: memloc=%04x has=%02x\n",operandSpecifier,inputBytes[operandSpecifier]);
        System.out.printf("Stored operand data: %02x\n", inputBytes[operandSpecifier]);
        }
    }
    public static void EndRegisterAddressInstruction(int output)
    {
        if (register == ACCUMULATOR_CHAR)
        {
            accumulator = output;
            System.out.println("A, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: A= %04x ,", accumulator);

            WriteNFlags();

        }
        else
        {
            indexRegister = output;
            System.out.println("X, OPcode is NOT unary");
            WriteOperandSpecifierInformation();
            WriteAddressingModeInformation();
            WriteLoadedOperandInformation();
            System.out.printf("After execution: X= %04x ,", indexRegister);
            WriteNFlags();

        }
    }
    public static void UpdatePC(int amount)
    {
     programCounter += amount;
    }
    public static void DisplayFetchedInstruction()
    {
     System.out.printf("\nFetched instruction at: %04x\n", programCounter);
     if (instructionSpecifier == 0)
     {
         System.out.println("STOP INSTRUCTION DECODED");
     }
     System.out.printf("Instruction OPcode in HEX is: %02x\n", instructionSpecifier);
    }

    public static void WriteNFlags()
    {
        System.out.print("N: " + (nFlag ? "1" : "0") + " Z: " + (zFlag ? "1" : "0") + " V: " + (vFlag ? "1" : "0") + " C: " + (cFlag ? "1" : "0"));
    }
    public static void WriteNZFlags()
    {
        System.out.print("N: " + (nFlag ? "1" : "0") + " Z: " + (zFlag ? "1" : "0"));
    }
    public static void WriteOperandSpecifierInformation()
    {
        System.out.printf("and the operand address/value is: %04x\n", operandSpecifier);
    }
    public static void WriteLoadedOperandInformation()
    {
        System.out.printf("Loaded operand Data: %04x\n", operand);
    }
    public static void WriteAddressingModeInformation()
    {
        switch(addressingMode)
        {
            case IMMEDIATE_ADDRESSING:
                System.out.println("Operand uses:IMMEDIATE addressing mode");
                break;
            case DIRECT_ADDRESSING:
                System.out.println("Operand uses:DIRECT addressing mode");
                break;
            case INDIRECT_ADDRESSING:
                System.out.printf("Operand uses:INDIRECT addressing mode, mem[mem[OP]]: %04x\n", intermediateOperand);
                break;
            case INDEXED_ADDRESSING:
                System.out.printf("Operand uses:INDEXED addressing mode, mem[OP+X]: %04x\n", intermediateOperand);
                break;
            default:
                break;
        }
    }
}
